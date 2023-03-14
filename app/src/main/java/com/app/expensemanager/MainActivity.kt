package com.app.expensemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.expensemanager.ui.expense.AddExpenseScreen
import com.app.expensemanager.ui.expense.ExpenseDetailScreen
import com.app.expensemanager.ui.expense.ExpenseScreen
import com.app.expensemanager.ui.theme.ExpenseManagerTheme
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseManagerTheme {
                MainScreenView()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreenView() {

        val parentNavController = rememberNavController()
        var canPop by remember { mutableStateOf(false) }

        DisposableEffect(parentNavController) {
            val listener = NavController.OnDestinationChangedListener { controller, _, _ ->
                canPop = controller.previousBackStackEntry != null
            }
            parentNavController.addOnDestinationChangedListener(listener)
            onDispose {
                parentNavController.removeOnDestinationChangedListener(listener)
            }
        }

        val navigationIcon: (@Composable () -> Unit)? =
            if (canPop) {
                {
                    IconButton(onClick = { parentNavController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            } else {
                null
            }


        Scaffold(topBar = {
            TopAppBar(
                title = {}, navigationIcon = navigationIcon,
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
            )
        }) {
            MainNavigationGraph(navController = parentNavController, paddingValues = it)
        }
    }

    @Composable
    fun MainNavigationGraph(navController: NavHostController, paddingValues: PaddingValues) {
        NavHost(
            navController,
            startDestination = "expenses",
            Modifier.padding(paddingValues)
        ) {
            composable("expenses") {
                ExpenseScreen(navController, viewModel)
            }

            composable(
                "add_expense?expenseId={expenseId}",
                arguments = listOf(navArgument("expenseId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                AddExpenseScreen(viewModel, it.arguments?.getString("expenseId"),navController)
            }
            composable("expense_detail") {
                ExpenseDetailScreen()
            }
        }
    }
}

