package com.app.expensemanager.ui.signin

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.app.expensemanager.MainActivity
import com.app.expensemanager.R
import com.app.expensemanager.data.ExpensePreferences
import com.app.expensemanager.data.models.LoginRequest
import com.app.expensemanager.data.network.NetworkResultState
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun SignInScreen(viewModel: AuthViewModel) {

    val context = LocalContext.current

    var username by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var showProgress by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = true) {
        viewModel.loginResult.collect {
            showProgress = false
            when (it) {
                is NetworkResultState.Loading -> {
                    showProgress = true
                }
                is NetworkResultState.Error -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
                is NetworkResultState.Success -> {
                    it?.data.let { res ->
                        runBlocking {
                            ExpensePreferences(context).apply {
                                res?.username?.let { saveUser(it) }
                                res?.accessToken?.let { saveAccessToken(it) }
                            }
                        }

                    }
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as Activity).finish()

                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign in to Expense Manager!",
                    modifier = Modifier.padding(vertical = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = Typography.headlineLarge
                )
            }

            Column(Modifier.weight(2f)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textStyle = Typography.bodyLarge,
                    placeholder = {
                        Text(text = "Username", style = Typography.bodyLarge)
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        disabledTextColor = MaterialTheme.colorScheme.onSecondary
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textStyle = Typography.bodyLarge,
                    placeholder = {
                        Text(text = "Password", style = Typography.bodyLarge)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.onSecondary,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        disabledTextColor = MaterialTheme.colorScheme.onSecondary
                    )
                )

                Button(
                    onClick = {
                        viewModel.login(LoginRequest(username, password))
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple40))
                ) {
                    Text(
                        text = "Sign In",
                        style = Typography.bodyLarge,
                        color = colorResource(id = R.color.white)
                    )
                }
            }

        }

        if (showProgress)
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            )
    }
}