package com.app.expensemanager.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha.medium
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.expensemanager.R
import com.app.expensemanager.ui.theme.Shapes
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.theme.bodyMediumWhite
import java.util.Calendar


@Composable
fun MonthPickerDialog(onCancel: () -> Unit,onUpdateMonth: (Int,Int) -> Unit) {
    val monthList = listOf(
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC"
    )
    val currentMonth = remember {
        mutableStateOf(Calendar.getInstance().get(Calendar.MONTH))
    }

    val currentYear = remember {
        mutableStateOf(Calendar.getInstance().get(Calendar.YEAR))
    }

    Dialog(onDismissRequest = { onCancel() }) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .background(colorResource(id = R.color.purple40), Shapes.medium)
                .padding(10.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text(text = "Select month and year", modifier = Modifier.padding(vertical = 10.dp), style = Typography.bodyMediumWhite)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_prev),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            if (currentMonth.value > 0) {
                                currentMonth.value--
                            }
                        },
                    contentDescription = "previous month"
                )
                Text(
                    modifier = Modifier.width(100.dp),
                    textAlign = TextAlign.Center,
                    text = monthList[currentMonth.value],
                    style = Typography.bodyMediumWhite
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_next),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            if (currentMonth.value < monthList.size - 1) {
                                currentMonth.value++
                            }
                        },
                    contentDescription = "next month"
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_prev),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            currentYear.value--
                        },
                    contentDescription = "previous year"
                )
                Text(
                    modifier = Modifier.width(100.dp),
                    textAlign = TextAlign.Center,
                    text = currentYear.value.toString(),
                    style = Typography.bodyMediumWhite
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_next),
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable {
                            currentYear.value++
                        },
                    contentDescription = "next year"
                )
            }
            Row(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Button(
                    onClick = {
                        onCancel()
                    }, modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple80))
                ) {
                    Text("Cancel", style = MaterialTheme.typography.bodyMediumWhite)
                }
                Button(
                    onClick = {
                        onUpdateMonth(currentMonth.value,currentYear.value)
                    }, modifier = Modifier
                        .padding(10.dp)
                        .width(100.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple80))
                ) {
                    Text("OK",style = MaterialTheme.typography.bodyMediumWhite)
                }
            }
        }

    }
}