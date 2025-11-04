package com.yogitechnolabs.loginmanager.ui

import android.app.TimePickerDialog
import android.content.Context
import java.util.*

object CustomTimePicker {

    /**
     * Show a Time Picker dialog and return selected time in callback
     */
    fun show(
        context: Context,
        is24Hour: Boolean = true,
        onTimeSelected: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour,
            minute,
            is24Hour
        )

        timePickerDialog.show()
    }
}
