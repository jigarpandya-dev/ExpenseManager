package com.app.expensemanager.ui.bottomnav

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.expensemanager.data.models.ExpenseResponse
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel


@Composable
fun ExpenseListScreen(viewModel: ExpenseViewModel) {

    var expenseResult by remember {
        mutableStateOf<NetworkResultState<List<ExpenseResponse>>>(NetworkResultState.Loading)
    }
    LaunchedEffect(key1 = true) {
        viewModel.expenseResult.collect {
            expenseResult = it
        }
    }

    when (val response = expenseResult) {
        is NetworkResultState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
        }

        is NetworkResultState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                response.data.let {
                    it.sortedBy { it.date!! }.let { it1 -> ExpenseList(it1) }
                }

            }
        }

        is NetworkResultState.Error -> {
            Toast.makeText(LocalContext.current, response.message, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun ExpenseList(expenseList: List<ExpenseResponse>) {

    // Create a MutableTransitionState<Boolean> for the AnimatedVisibility.
    val state = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    AnimatedVisibility(visibleState = state, enter = slideInHorizontally()) {
        LazyColumn() {
            items(expenseList.size) { index ->
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
                            .fillMaxWidth(),
                    ) {
                        Text(
                            text = expenseList[index].title!!,
                            color = MaterialTheme.colorScheme.surface,
                            textAlign = TextAlign.Start,
                            style = Typography.bodyLarge
                        )
                        Text(
                            text = expenseList[index].date?.substring(
                                0,
                                expenseList[index].date!!.indexOf('T')
                            )!!,
                            color = MaterialTheme.colorScheme.surface,
                            textAlign = TextAlign.Start,
                            style = Typography.bodyLarge
                        )
                        Text(
                            text = expenseList[index].category!!,
                            color = MaterialTheme.colorScheme.surface,
                            textAlign = TextAlign.Start,
                            style = Typography.bodyLarge
                        )
                        Text(
                            text = expenseList[index].transactionType!!,
                            color = MaterialTheme.colorScheme.surface,
                            textAlign = TextAlign.Start,
                            style = Typography.bodyLarge
                        )
                    }

                    Text(
                        text = expenseList[index].amount.toString() + " /-",
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

}