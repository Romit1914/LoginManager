package com.yogitechnolabs.loginmanager.ui

import android.util.Log

object Logger {

    private const val TAG = "AppLogger"       // all logs except errors
    private const val ERROR_TAG = "AppLogger_ERROR" // only errors
    private var isLoggingEnabled = true

    /**
     * Call once in Application class if you want to disable logs in release build
     */
    fun init(enableLogs: Boolean) {
        isLoggingEnabled = enableLogs
    }

    fun d(message: String) {
        if (isLoggingEnabled) Log.d(TAG, formatMessage(message))
    }

    fun i(message: String) {
        if (isLoggingEnabled) Log.i(TAG, formatMessage(message))
    }

    fun w(message: String) {
        if (isLoggingEnabled) Log.w(TAG, formatMessage(message))
    }

    fun v(message: String) {
        if (isLoggingEnabled) Log.v(TAG, formatMessage(message))
    }

    fun e(message: String, throwable: Throwable? = null) {
        if (isLoggingEnabled) Log.e(ERROR_TAG, formatMessage(message), throwable)
    }

    /**
     * Automatically adds file name and line number
     */
    private fun formatMessage(message: String): String {
        val stackTrace = Thread.currentThread().stackTrace
        val element = stackTrace.firstOrNull { it.className != Logger::class.java.name && !it.className.contains("java.lang.Thread") }
        val fileName = element?.fileName ?: "UnknownFile"
        val lineNumber = element?.lineNumber ?: -1
        return "($fileName:$lineNumber) → $message"
    }
}
