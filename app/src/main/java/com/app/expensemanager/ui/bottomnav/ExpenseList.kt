package com.app.expensemanager.ui.bottomnav

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensemanager.R
import com.app.expensemanager.data.models.ExpenseResponse
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.common.MonthPickerDialog
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*



@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel, parentNavHostController: NavHostController) {

    // this is to prevent re-initialization of expenseList and categoryList during recomposition

    val expenseList = remember { mutableStateListOf<ExpenseResponse>() }
    val categoryWiseList = remember { mutableStateListOf<ExpenseResponse>() }

    var state by remember { mutableStateOf(0) }
    var showProgress by remember { mutableStateOf(false) }
    val titles = listOf("All", "Category")

    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing.value)

    LaunchedEffect(key1 = true) {
        viewModel.expenseResult.collect {
            when (val response = it) {
                is NetworkResultState.Loading -> {
                     showProgress = true
                }

                is NetworkResultState.Success -> {
                    showProgress = false
                    response.data.let { list ->
                        categoryWiseList.clear()
                        expenseList.clear()

                        list.sortedByDescending { it.date!! }

                        list.distinctBy {
                            it.category
                        }.forEach { categoryExpense ->
                            categoryWiseList.add(categoryExpense.copy(isHeader = true))
                            categoryWiseList.addAll(list.filter { it.category == categoryExpense.category })
                        }
                        expenseList.addAll(list)
                    }
                }

                is NetworkResultState.Error -> {
                    showProgress = false
                    // Toast.makeText(LocalContext.current, response.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    /** Calendar logic to change the month */

    val c = Calendar.getInstance()

    var year by remember { mutableStateOf(c.get(Calendar.YEAR)) }
    var month by remember { mutableStateOf(c.get(Calendar.MONTH)) }

    var currentMonthLabel by remember { mutableStateOf("${SimpleDateFormat("MMM").format(c.time)} $year") }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        MonthPickerDialog(onCancel = {
            showDialog = false
        }, onUpdateMonth = { newMonth: Int, newYear: Int ->
            showDialog = false
            month = newMonth
            year = newYear

            c.set(Calendar.MONTH, month)
            c.set(Calendar.YEAR, year)

            currentMonthLabel = "${SimpleDateFormat("MMM").format(c.time)} $year"
            viewModel.getAllExpenses(month, year)
        })
    }

    if(showProgress){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {

        Row(
            modifier = Modifier.border(
                border = BorderStroke(
                    width = 1.dp, colorResource(id = R.color.purple40)
                )
            )
        ) {
            TabRow(selectedTabIndex = state,
                modifier = Modifier.padding(5.dp),
                backgroundColor = MaterialTheme.colorScheme.surface,
                indicator = {}) {
                titles.forEachIndexed { index, title ->
                    val color =
                        if (state == index) colorResource(id = R.color.purple40) else MaterialTheme.colorScheme.surface
                    Column(modifier = Modifier.background(color)) {
                        Tab(selected = state == index, onClick = {
                            state = index
                        }, selectedContentColor = colorResource(id = R.color.purple80), text = {
                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = Typography.bodyLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        })
                    }

                }
            }
        }

        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .width(200.dp)
                .padding(10.dp),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 12.dp,
                end = 20.dp,
                bottom = 12.dp
            ),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple40))
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material.Icon(
                    imageVector = Icons.Default.DateRange,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.CenterEnd),
                    contentDescription = "date picker",
                    tint = Color.White
                )
                Text(currentMonthLabel, color = Color.White, style = Typography.bodyMedium)
            }

        }

        if (state == 0) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = { viewModel.getAllExpenses(month,year) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 16.dp,
                    )
            ) {
                ExpenseList(
                    expenseList = expenseList,
                    parentNavController = parentNavHostController
                )
            }
        } else CategoryList(
            viewModel = viewModel, categoryList = categoryWiseList
        )


    }
}

@Composable
fun ExpenseList(expenseList: List<ExpenseResponse>, parentNavController: NavHostController) {
    LazyColumn(modifier = Modifier.padding(vertical = 10.dp)) {
        items(expenseList) { expense ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            parentNavController.navigate("add_expense?expenseId=" + expense.id)
                        },
                ) {
                    Text(
                        text = expense.title!!,
                        color = MaterialTheme.colorScheme.surface,
                        textAlign = TextAlign.Start,
                        style = Typography.bodyLarge
                    )
                    Text(
                        text = expense.date?.substring(
                            0, expense.date!!.indexOf('T')
                        )!!,
                        color = MaterialTheme.colorScheme.surface,
                        textAlign = TextAlign.Start,
                        style = Typography.bodyLarge
                    )
                    Text(
                        text = expense.category!!,
                        color = MaterialTheme.colorScheme.surface,
                        textAlign = TextAlign.Start,
                        style = Typography.bodyLarge
                    )
                    Text(
                        text = expense.transactionType!!,
                        color = MaterialTheme.colorScheme.surface,
                        textAlign = TextAlign.Start,
                        style = Typography.bodyLarge
                    )
                }

                Text(
                    text = expense.amount.toString() + " /-",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.End,
                    style = Typography.bodyLarge
                )
            }
        }
    }

}

@Composable
fun CategoryList(
    viewModel: ExpenseViewModel,
    categoryList: List<ExpenseResponse>,
) {

    val expandedCardIds by viewModel.expandedCardIdsList.collectAsStateWithLifecycle()

    Spacer(modifier = Modifier.height(10.dp))
    LazyColumn() {
        items(categoryList) { expense ->
            ExpandableCard(
                expense = expense,
                onCardClick = { viewModel.onCardArrowClicked(expense.category!!) },
                isExpanded = expandedCardIds.contains(expense.category)
            )
        }
    }

}


@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(expense: ExpenseResponse, onCardClick: () -> Unit, isExpanded: Boolean) {

    /** Header Item **/
    if (expense.isHeader) {

        val transitionState = remember {
            MutableTransitionState(isExpanded).apply {
                targetState = !isExpanded
            }
        }
        val transition = updateTransition(transitionState, label = "")

        val arrowRotationDegree by transition.animateFloat({
            tween(durationMillis = 500)
        }, label = "") {
            if (!isExpanded) 0f else 90f
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(4.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = expense.category!!,
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.Start,
                    style = Typography.bodyLarge
                )

            }

            IconButton(onClick = {
                onCardClick()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_next),
                    modifier = Modifier.rotate(arrowRotationDegree),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(2.dp))

    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top, animationSpec = tween(500)
        ) + fadeIn(
            initialAlpha = 0.3f, animationSpec = tween(500)
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            // Expand from the top.
            shrinkTowards = Alignment.Top, animationSpec = tween(500)
        ) + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            animationSpec = tween(500)
        )
    }

    /** List Items **/
    AnimatedVisibility(
        visible = isExpanded && !expense.isHeader, enter = enterTransition, exit = exitTransition
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(4.dp)
                )
        ) {
            Column(
                modifier = Modifier.padding(10.dp)

            ) {
                Text(
                    text = expense.date?.substring(
                        0, expense.date!!.indexOf('T')
                    )!!,
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.Start,
                    style = Typography.bodyLarge
                )
                Text(
                    text = expense.title!!,
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.Start,
                    style = Typography.bodyLarge
                )
                Text(
                    text = expense.transactionType!!,
                    color = MaterialTheme.colorScheme.surface,
                    textAlign = TextAlign.Start,
                    style = Typography.bodyLarge
                )
            }
        }
    }
}
