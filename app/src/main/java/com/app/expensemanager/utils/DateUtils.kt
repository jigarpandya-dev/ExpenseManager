package com.app.expensemanager.utils

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        fun getDate(date: String): Date {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date = parser.parse(date)

            return date
        }

        fun isDateInCurrentWeek(date: Date?): Boolean {
            val currentCalendar: Calendar = Calendar.getInstance()
            val week: Int = currentCalendar.get(Calendar.WEEK_OF_YEAR)
            val year: Int = currentCalendar.get(Calendar.YEAR)
            val targetCalendar: Calendar = Calendar.getInstance()
            targetCalendar.time = date
            val targetWeek: Int = targetCalendar.get(Calendar.WEEK_OF_YEAR)
            val targetYear: Int = targetCalendar.get(Calendar.YEAR)
            return week == targetWeek && year == targetYear
        }


        fun isDateInCurrentMonth(date: Date?): Boolean {
            val currentCalendar: Calendar = Calendar.getInstance()
            val month: Int = currentCalendar.get(Calendar.MONTH)

            val targetCalendar: Calendar = Calendar.getInstance()
            targetCalendar.time = date
            val targetMonth: Int = targetCalendar.get(Calendar.MONTH)

            return month == targetMonth
        }

        fun getDateValue(date: String?): String {
            return if (date.isNullOrBlank())
                ""
            else if (date.contains('T'))
                date.substring(0, date.indexOf('T'))
            else date

        }
    }
}