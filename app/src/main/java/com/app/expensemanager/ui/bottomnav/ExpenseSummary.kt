package com.app.expensemanager.ui.bottomnav

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.expensemanager.data.models.ExpenseResponse
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.utils.DateUtils
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ExpenseSummaryScreen(viewModel: ExpenseViewModel) {

    val context = LocalContext.current
    // Create a MutableTransitionState<Boolean> for the AnimatedVisibility.
    val state = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    var thisWeekExpenseList by remember { mutableStateOf(emptyList<ExpenseResponse>()) }
    var thisMonthExpenseList by remember { mutableStateOf(emptyList<ExpenseResponse>()) }

    LaunchedEffect(key1 = true){
        viewModel.expenseResult.collect { expenseResult->
            when (expenseResult) {
                is NetworkResultState.Success -> {
                        expenseResult.data.let {
                            thisWeekExpenseList =
                                it.filter { expense ->
                                    DateUtils.isDateInCurrentWeek(
                                        DateUtils.getDate(
                                            expense.date!!
                                        )
                                    )
                                }!!
                            thisMonthExpenseList =
                                it.filter { expense ->
                                    DateUtils.isDateInCurrentMonth(
                                        DateUtils.getDate(
                                            expense.date!!
                                        )
                                    )
                                }

                        }
                }
                is NetworkResultState.Error -> {
                    Toast.makeText(context, expenseResult.message, Toast.LENGTH_LONG).show()
                }
                else ->{

                }
            }
        }
    }


    Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.SpaceEvenly) {
        Text(
            text = "Hi, Jigar!",
            color = MaterialTheme.colorScheme.onSurface,
            style = Typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(visibleState = state, enter = slideInVertically()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                backgroundColor = MaterialTheme.colorScheme.secondary,
                elevation = 10.dp
            ) {
                val highestExpense = thisWeekExpenseList.maxByOrNull { it.amount ?: 0.0 }
                val thisWeekTotal = thisWeekExpenseList.sumOf { it.amount ?: 0.0 }

                Column() {
                    Text(
                        text = "This week's summary",
                        Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        style = Typography.bodyLarge
                    )

                    highestExpense?.let {
                        Text(
                            text = "Highest expense : ${highestExpense?.title} ${highestExpense?.amount}",
                            Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            style = Typography.bodyLarge
                        )

                        Text(
                            text = "Total : $thisWeekTotal",
                            Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            style = Typography.bodyLarge
                        )
                    }

                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(visibleState = state, enter = slideInVertically()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                backgroundColor = MaterialTheme.colorScheme.secondary,
                elevation = 10.dp
            ) {
                val highestExpense = thisMonthExpenseList.maxByOrNull { it.amount ?: 0.0 }
                val thisWeekTotal = thisMonthExpenseList.sumOf { it.amount ?: 0.0 }

                Column() {
                    Text(
                        text = "This month's summary",
                        Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        style = Typography.bodyLarge
                    )

                    highestExpense?.let {
                        Text(
                            text = "Highest expense : ${highestExpense?.title} ${highestExpense?.amount}",
                            Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            style = Typography.bodyLarge
                        )

                        Text(
                            text = "Total : $thisWeekTotal",
                            Modifier.padding(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            style = Typography.bodyLarge
                        )
                    }

                }
            }
        }

    }


}