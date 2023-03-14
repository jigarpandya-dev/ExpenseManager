package com.app.expensemanager.ui.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.expensemanager.ui.bottomnav.BottomNavItem
import com.app.expensemanager.ui.bottomnav.ExpenseListScreen
import com.app.expensemanager.ui.bottomnav.ExpenseSummaryScreen
import com.app.expensemanager.ui.bottomnav.SettingScreen
import com.app.expensemanager.ui.theme.Typography
import com.app.expensemanager.ui.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(parentNavController: NavHostController, viewModel: ExpenseViewModel) {
    val items = listOf(
        BottomNavItem.Summary,
        BottomNavItem.ExpenseList,
        BottomNavItem.Settings,
    )

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(20.dp)) {
                NavigationBar(
                    modifier = Modifier.clip(
                        RoundedCornerShape(30.dp)
                    )
                ) {

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = screen.title,
                                    modifier = Modifier
                                        .padding(horizontal = 5.dp)
                                        .width(25.dp)
                                        .height(25.dp)
                                )
                            },
                            label = { Text(screen.title, style = Typography.bodyLarge) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.screen_route } == true,
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate(screen.screen_route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == BottomNavItem.Summary.screen_route) //show floating action button on summery screen only
                FloatingActionButton(
                    // on below line we are adding on click for our fab
                    onClick = {
                        parentNavController.navigate("add_expense")
                    },
                ) {
                    // on below line we are
                    // adding icon for button.
                    Icon(Icons.Filled.Add, "")
                }
        }
    ) {
        ExpenseNavigationGraph(navController = navController, parentNavController,it, viewModel)
    }
}


@Composable
fun ExpenseNavigationGraph(
    navController: NavHostController,
    parentNavController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: ExpenseViewModel
) {
    NavHost(
        navController,
        startDestination = BottomNavItem.Summary.screen_route,
        Modifier.padding(paddingValues)
    ) {
        composable(BottomNavItem.Summary.screen_route) {
            ExpenseSummaryScreen(viewModel)
        }
        composable(BottomNavItem.ExpenseList.screen_route) {
            ExpenseListScreen(viewModel,parentNavController)
        }
        composable(BottomNavItem.Settings.screen_route) {
            SettingScreen()
        }
    }
}