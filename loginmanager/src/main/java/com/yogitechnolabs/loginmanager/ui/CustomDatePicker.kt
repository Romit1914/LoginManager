package com.yogitechnolabs.loginmanager.ui

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object CustomDatePicker {

    /**
     * Show a Date Picker dialog and return selected date in callback
     * Format: yyyy-MM-dd
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
                // Add leading zeros if needed
                val mm = String.format("%02d", selectedMonth + 1)
                val dd = String.format("%02d", selectedDay)
                val formattedDate = "$selectedYear-$mm-$dd"
                onDateSelected(formattedDate)
            },
            year, month, day
        )

        datePickerDialog.show()
    }
}
