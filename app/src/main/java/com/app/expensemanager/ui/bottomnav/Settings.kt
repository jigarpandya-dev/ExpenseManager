package com.app.expensemanager.ui.bottomnav


import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.app.expensemanager.R
import com.app.expensemanager.SignInActivity
import com.app.expensemanager.data.ExpensePreferences
import com.app.expensemanager.ui.signin.SignInScreen
import com.app.expensemanager.ui.theme.Typography
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun SettingScreen() {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Button(
            onClick = {
                coroutineScope.launch { ExpensePreferences(context).clearData() }
                context.startActivity(Intent(context, SignInActivity::class.java))
                (context as Activity).finish()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple40))
        ) {
            Text(
                text = "Log out ?",
                style = Typography.bodyLarge,
                color = colorResource(id = R.color.white)
            )
        }
    }
}

