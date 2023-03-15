package com.app.expensemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensemanager.data.ExpensePreferences
import com.app.expensemanager.data.models.BaseResponse
import com.app.expensemanager.data.models.Expense
import com.app.expensemanager.data.models.ExpenseResponse
import com.app.expensemanager.data.models.UIEvent
import com.app.expensemanager.data.network.ExpenseRepository
import com.app.expensemanager.data.network.NetworkResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val preferences: ExpensePreferences
) :
    ViewModel() {

    private var _validationError = MutableSharedFlow<String>()
    val validationError: SharedFlow<String> = _validationError

    private var _addExpenseResult = MutableSharedFlow<NetworkResultState<Expense>>()
    val addExpenseResult: SharedFlow<NetworkResultState<Expense>> = _addExpenseResult

    private var _deleteExpenseResult = MutableSharedFlow<NetworkResultState<BaseResponse>>()
    val deleteExpenseResult: SharedFlow<NetworkResultState<BaseResponse>> = _deleteExpenseResult

    private var _expenseResult =
        MutableStateFlow<NetworkResultState<List<ExpenseResponse>>>(NetworkResultState.Loading)
    val expenseResult: SharedFlow<NetworkResultState<List<ExpenseResponse>>> = _expenseResult

    private val _expandedCardIdsList = MutableStateFlow(listOf<String>())
    val expandedCardIdsList: StateFlow<List<String>> get() = _expandedCardIdsList

    private val _uiEventState = MutableStateFlow<UIEvent>(UIEvent.Idle)
    val uiEventState:StateFlow<UIEvent> = _uiEventState

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()


    init {
        getAllExpenses()
    }

    fun onUIEvent(event: UIEvent) {
        _uiEventState.value = event
        if(event is UIEvent.Submit) {
            validateExpense(event.expense, event.id)
        }
    }

    private fun validateExpense(expense: Expense,id:String?) {
        viewModelScope.launch {
            if (expense.date.isNullOrBlank()) {
                _validationError.emit("Please select expense date.")
            } else if (expense.title.isNullOrBlank()) {
                _validationError.emit("Please enter expense title.")
            } else if (expense.category.isNullOrBlank()) {
                _validationError.emit("Please select expense category")
            } else if (expense.amount?.isNaN() == null || expense.amount?.isNaN() == true) {
                _validationError.emit("Please enter valid expense amount.")
            } else if (expense.transactionType.isNullOrBlank()) {
                _validationError.emit("Please select expense transaction type.")
            } else {
                if(id.isNullOrBlank())
                     addExpense(expense)
                else
                    updateExpense(id,expense)
            }
        }
    }

    private fun addExpense(expense: Expense) {
        viewModelScope.launch {
            preferences.currentUser.collect {
                it?.let {
                    expense.user = it
                    repository.addExpense(expense).collect {
                        _addExpenseResult.emit(it)
                    }
                }

            }

        }
    }

    private fun updateExpense(id:String,expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(id,expense).collect {
                _addExpenseResult.emit(it)
            }
        }
    }

    fun deleteExpense(id:String){
        viewModelScope.launch {
            repository.deleteExpense(id).collect{
                _deleteExpenseResult.emit(it)
            }
        }
    }

    fun getAllExpenses() {
        _isRefreshing.update { true }
        viewModelScope.launch {
            preferences.currentUser.collect {
                it?.let {
                    repository.getAllExpenses(it).collect { res ->
                        _isRefreshing.update { false }
                        when (res) {
                            is NetworkResultState.Error -> {
                                _expenseResult.update { res.copy(message = res.message) }
                            }
                            is NetworkResultState.Success -> {
                                _expenseResult.update { res.copy(data = res.data) }
                            }
                            else->{

                            }
                        }
                    }
                }
            }

        }
    }

    fun getExpenseDetails(expenseId: String): ExpenseResponse? {
        _expenseResult.value.let {
            if (it is NetworkResultState.Success) {
                val expenseList = it?.data
                return expenseList?.find { it -> it.id == expenseId }
            }
        }

        return null
    }

    fun onCardArrowClicked(cardId: String) {
        _expandedCardIdsList.value = _expandedCardIdsList.value.toMutableList().also { list ->
            if (list.contains(cardId)) list.remove(cardId) else list.add(cardId)
        }
    }
}