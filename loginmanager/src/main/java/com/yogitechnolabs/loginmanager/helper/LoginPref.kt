package com.yogitechnolabs.loginmanager.helper

import android.content.Context

object LoginPref {

    private const val PREF_NAME = "user_login_pref"
    private const val KEY_EMAIL = "email"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveLoginData(context: Context, email: String, userId: String, token: String?) {
        prefs(context).edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_USER_ID, userId)
            putString(KEY_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun getEmail(context: Context): String? = prefs(context).getString(KEY_EMAIL, null)
    fun getUserId(context: Context): String? = prefs(context).getString(KEY_USER_ID, null)
    fun getToken(context: Context): String? = prefs(context).getString(KEY_TOKEN, null)
}
