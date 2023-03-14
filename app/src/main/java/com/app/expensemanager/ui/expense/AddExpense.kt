package com.app.expensemanager.ui.expense


import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import com.app.expensemanager.R
import com.app.expensemanager.data.models.Expense
import com.app.expensemanager.data.models.UIEvent
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.common.SimpleAlertDialog
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel
import com.app.expensemanager.utils.DateUtils
import com.app.expensemanager.utils.DateUtils.Companion.getDateValue
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    expenseId: String?,
    parentNavController: NavHostController
) {

    val context = LocalContext.current
    var expense by remember {
        mutableStateOf(Expense())
    }

    LaunchedEffect(true) {
        launch {
            viewModel.addExpenseResult.collect {
                when (it) {
                    is NetworkResultState.Error -> {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResultState.Success -> {
                        Toast.makeText(context, "Expense added !!", Toast.LENGTH_SHORT).show()
                        viewModel.getAllExpenses()
                        parentNavController.popBackStack()
                    }
                    else ->{

                    }

                }
            }
        }

        launch {
            viewModel.deleteExpenseResult.collect {
                when (it) {
                    is NetworkResultState.Error -> {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    is NetworkResultState.Success -> {
                        Toast.makeText(context, it.data.message, Toast.LENGTH_SHORT).show()
                        viewModel.getAllExpenses()
                        parentNavController.popBackStack()
                    }
                    else->{

                    }

                }
            }
        }

        launch {
            viewModel.validationError.collect {
                if (!it.isNullOrBlank()) {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        expenseId?.let {
            val expenseDetails = viewModel.getExpenseDetails(expenseId)
            expenseDetails?.let {
                val tempExpense = Expense().apply {
                    date = it.date
                    title = it.title
                    category = it.category
                    amount = it.amount
                    transactionType = it.transactionType
                    notes = it.notes

                }
                expense = tempExpense
            }
        }

    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        var showDialog = remember { mutableStateOf(false) }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            val dateValue = getDateValue(expense.date)
            ExpenseInput(hint = "Date", value = dateValue ?: "", isDatePicker = true) {
                expense.date = it
            }
            ExpenseInput(hint = "Title", value = expense.title ?: "") {
                expense.title = it.trim()
            }
            ExpenseInput(
                hint = "Category",
                value = expense.category ?: "",
                isCategoryDropdown = true
            ) {
                expense.category = it
            }
            val amountValue = if (expense.amount != null) expense.amount.toString() else ""
            ExpenseInput(
                hint = "Amount",
                value = amountValue, isNumberType = true
            ) {
                expense.amount = it.toDouble()
            }
            ExpenseInput(
                hint = "Transaction type",
                value = expense.transactionType ?: "",
                isTranTypeDropdown = true
            ) {
                expense.transactionType = it
            }
            ExpenseInput(hint = "Notes", value = expense.notes ?: "") {
                expense.notes = it
            }

        }

        Button(
            onClick = {
                viewModel.onUIEvent(UIEvent.Submit(expense, expenseId))
            }, modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple40))
        ) {
            Text(
                text = if (!expenseId.isNullOrEmpty()) "Update Expense" else "Add Expense",
                style = Typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
        }

        Spacer(Modifier.height(10.dp))

        if (!expenseId.isNullOrEmpty())
            Button(
                onClick = {
                    showDialog.value = true
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = android.R.color.holo_red_dark))
            ) {
                Text(
                    text = "Delete Expense",
                    style = Typography.bodyLarge,
                    color = colorResource(id = R.color.white)
                )
            }

        if (showDialog.value) {
            SimpleAlertDialog(onConfirm = {
                viewModel.deleteExpense(expenseId!!)
            }, onCancel = {
                showDialog.value = false
            })
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun ExpenseInput(
    hint: String,
    value: String = "",
    isNumberType: Boolean = false,
    isDatePicker: Boolean = false,
    isCategoryDropdown: Boolean = false,
    isTranTypeDropdown: Boolean = false,
    callback: (String) -> Unit
) {
    var data by remember { mutableStateOf(value) }
    var showDate by remember { mutableStateOf(false) }
    var mExpanded by remember { mutableStateOf(false) }
    var showTransType by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    if (value.isNotBlank())
        data = value

    val c = Calendar.getInstance()

    Box() {
        OutlinedTextField(
            value = data,
            onValueChange = {
                data = it
                callback(data)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDate = isDatePicker
                    showTransType = isTranTypeDropdown
                    mExpanded = isCategoryDropdown

                }
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                },
            singleLine = true,
            textStyle = Typography.bodyLarge,
            placeholder = {
                Text(text = hint, style = Typography.bodyLarge)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary,
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                disabledTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondary
            ),
            readOnly = isDatePicker || isCategoryDropdown || isTranTypeDropdown,
            enabled = !(isDatePicker || isCategoryDropdown || isTranTypeDropdown)
        )

        if (mExpanded)
            CategoryDropdownUI(textFieldSize) {
                mExpanded = false
                if (it != null) {
                    data = it
                    callback(it)
                }
            }

        if (showTransType)
            TransactionTypeDropdownUI(textFieldSize) {
                showTransType = false
                if (it != null) {
                    data = it
                    callback(it)
                }
            }
    }

    Spacer(Modifier.height(20.dp))

    if (showDate)
        DateUI(c = c) {
            showDate = false
            if (it != null) {
                data = it
                callback(it)
            }
        }

}

@Composable
fun DateUI(c: Calendar, callback: (String?) -> Unit) {

    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current, DatePickerDialog.OnDateSetListener
        { _, year: Int, month: Int, day: Int ->
            //onUpdateMonth(month+1)
            c.set(Calendar.DAY_OF_MONTH, day)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.YEAR, year)
            callback("$year- ${month + 1}-$day")
        }, year, month, day
    )
    //datePickerDialog.datePicker.minDate = c.timeInMillis
    datePickerDialog.setOnCancelListener {
        callback(null)
    }
    datePickerDialog.show()
}

@Composable
fun CategoryDropdownUI(textFieldSize: Size, callback: (String?) -> Unit) {

    val categories = listOf(
        "Grocery",
        "Food & Restaurant",
        "Grooming & Clothing",
        "Fuel",
        "Investment",
        "Travel",
        "Bills",
        "Medical",
        "Tax",
        "Fees",
        "EMIs",
        "Entertainment",
        "Entry Tickets",
        "Household",
        "Learning",

        )

    DropdownMenu(
        expanded = true,
        onDismissRequest = { callback(null) },
        modifier = Modifier
            .background(androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            .requiredHeightIn(max = 700.dp)


    ) {
        categories.sorted().forEach { category ->
            DropdownMenuItem(onClick = {
                callback(category)
            }) {
                Text(
                    text = category,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                    style = Typography.bodyLarge
                )
            }

        }
    }

}

@Composable
fun TransactionTypeDropdownUI(textFieldSize: Size, callback: (String?) -> Unit) {

    val categories = listOf("Cash", "Credit Card", "Debit Card", "Net Banking", "UPI")

    DropdownMenu(
        expanded = true,
        onDismissRequest = { callback(null) },
        modifier = Modifier
            .background(androidx.compose.material3.MaterialTheme.colorScheme.onSecondary)
            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            .requiredHeightIn(max = 700.dp)


    ) {
        categories.sorted().forEach { category ->
            DropdownMenuItem(onClick = {
                callback(category)
            }) {
                Text(
                    text = category,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                    style = Typography.bodyLarge
                )
            }

        }
    }
}