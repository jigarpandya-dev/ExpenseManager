package com.app.expensemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.expensemanager.data.ExpensePreferences
import com.app.expensemanager.ui.theme.ExpenseManagerTheme
import com.app.expensemanager.ui.signin.SignInScreen
import com.app.expensemanager.ui.viewmodel.AuthViewModel
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            val user = ExpensePreferences(this@SignInActivity).currentUser.first()
            if (user != null) {
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                finish()
            }
        }

        setContent {
            ExpenseManagerTheme {
                SignInScreen(viewModel)
            }
        }


    }
}