package com.app.expensemanager.ui.bottomnav

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.app.expensemanager.R
import com.app.expensemanager.data.models.ExpenseResponse
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.theme.*
import com.app.expensemanager.utils.DateUtils
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
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

    var showLoading by remember {
        mutableStateOf(true)
    }
    var thisWeekExpenseList by remember { mutableStateOf(emptyList<ExpenseResponse>()) }
    var thisMonthExpenseList by remember { mutableStateOf(emptyList<ExpenseResponse>()) }
    var thisMonthChartData by remember { mutableStateOf(emptyList<CategoryTotal>()) }

    val colorList = listOf(Purple80, Pink40, PurpleGrey40)

    LaunchedEffect(key1 = true) {
        viewModel.expenseResult.collect { expenseResult ->
            when (expenseResult) {
                is NetworkResultState.Success -> {
                    showLoading = false
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


                        val chartData = mutableListOf<CategoryTotal>()
                        thisMonthExpenseList.distinctBy {
                            it.category
                        }.forEach {
                            var total = 0.0
                            for (expense in thisMonthExpenseList) {
                                if (it.category == expense.category) {
                                    total += expense.amount!!
                                }
                            }
                            chartData.add(CategoryTotal(it.category!!, total))
                        }

                        val dataSize = if (chartData.size > 3) 3 else chartData.size

                        thisMonthChartData = chartData.sortedBy { it.total }.takeLast(dataSize)

                    }
                }
                is NetworkResultState.Error -> {
                    Toast.makeText(context, expenseResult.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    showLoading = true
                }
            }
        }
    }

    if (showLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
        }
    } else {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Hi, Jigar!",
                color = MaterialTheme.colorScheme.onSurface,
                style = Typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            val slices = mutableListOf<PieChartData.Slice>()
            for ((index, data) in thisMonthChartData.withIndex()) {
                slices.add(PieChartData.Slice(data.total.toFloat(), colorList[index]))
            }
            PieChart(
                pieChartData = PieChartData(slices),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            for ((index, data) in thisMonthChartData.withIndex()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .width(20.dp)
                            .height(10.dp)
                            .background(colorList[index])
                    )
                    Text(
                        text = data.category, color = MaterialTheme.colorScheme.onSurface,
                        style = Typography.bodyMedium
                    )
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


}

data class CategoryTotal(val category: String, val total: Double, val color: Color = Purple80)