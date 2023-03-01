package com.app.expensemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensemanager.data.ExpensePreferences
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

    private var _expenseResult =
        MutableStateFlow<NetworkResultState<List<ExpenseResponse>>>(NetworkResultState.Loading)
    val expenseResult: SharedFlow<NetworkResultState<List<ExpenseResponse>>> = _expenseResult


    init {
        getAllExpenses()
    }

    fun onUIEvent(event: UIEvent.Submit) {
        validateExpense(event.expense)
    }

    private fun validateExpense(expense: Expense) {
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
                addExpense(expense)
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

    fun getAllExpenses() {
        viewModelScope.launch {
            preferences.currentUser.collect {
                it?.let {
                    repository.getAllExpenses(it).collect { res ->
                        when (res) {
                            is NetworkResultState.Error -> {
                                _expenseResult.update { res.copy(message = res.message) }
                            }
                            is NetworkResultState.Success -> {
                                _expenseResult.update { res.copy(data = res.data) }
                            }
                        }
                    }
                }
            }

        }
    }
}