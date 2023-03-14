package com.app.expensemanager.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.app.expensemanager.R
import com.app.expensemanager.ui.theme.Typography

@Composable
fun SimpleAlertDialog(message: String? = null, onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        backgroundColor = colorResource(id = R.color.purple40),
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            })
            {
                Text(
                    text = "OK",
                    style = Typography.bodyLarge,
                    color = colorResource(id = R.color.white)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onCancel()
            })
            {
                Text(
                    text = "Cancel",
                    style = Typography.bodyLarge,
                    color = colorResource(id = R.color.white)
                )
            }
        },
        title = {
            Text(
                text = "Please confirm",
                style = Typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
        },
        text = {
            Text(
                text = message ?: "Are you sure you want to proceed ?",
                style = Typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
        },
        shape = RoundedCornerShape(10.dp)
    )
}