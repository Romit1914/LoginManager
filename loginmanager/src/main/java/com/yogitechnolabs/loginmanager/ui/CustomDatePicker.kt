package com.yogitechnolabs.loginmanager.ui

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object CustomDatePicker {

    /**
     * Show a Date Picker dialog and return selected date in callback
     */
    fun show(
        context: Context,
        onDateSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                onDateSelected(selectedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
