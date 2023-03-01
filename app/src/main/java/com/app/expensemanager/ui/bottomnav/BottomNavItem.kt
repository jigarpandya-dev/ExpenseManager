package com.app.expensemanager.ui.bottomnav

import com.app.expensemanager.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

    object Summary : BottomNavItem("Summary", R.drawable.ic_summary,"summary")
    object ExpenseList: BottomNavItem("Expense List",R.drawable.ic_calendar,"expense_list")
    object Settings: BottomNavItem("Settings",R.drawable.ic_settings,"settings")


}
