package com.yogitechnolabs.components.classes

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.yogitechnolabs.components.R


object LoginManager {

    private val database = mutableMapOf<String, MutableList<Map<String, String>>>()

    // --------- Email/Password Login ---------
    fun loginWithEmail(
        email: String,
        password: String,
        callback: (success: Boolean, message: String) -> Unit
    ) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,}$".toRegex()

        when {
            email.isEmpty() || password.isEmpty() -> {
                callback(false, "Email or password cannot be empty")
            }
            !email.matches(emailRegex) -> {
                callback(false, "Invalid email format")
            }
            !password.matches(passwordRegex) -> {
                callback(false, "Password must be at least 6 characters, include 1 uppercase letter, 1 number, and 1 special character")
            }
            else -> {
                callback(true, "Login successful")
            }
        }
    }

    fun attachForm(
        context: Context,
        btnSubmit: Button,
        etFirstName: EditText,
        etLastName: EditText,
        etGender: EditText,
        etDob: EditText,
        tableName: String
    ) {
        btnSubmit.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val gender = etGender.text.toString().trim()
            val dob = etDob.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty() || dob.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveUserDataDB(context,tableName, firstName, lastName, gender, dob)



            Toast.makeText(context, "Data saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveUserDataDB(context: Context, tableName: String, f: String, l: String, g: String, d: String) {
        val dbHelper = DatabaseHelper(context)
        dbHelper.insertUser(tableName, f, l, g, d)
    }

    fun getAllData(context: Context, tableName: String): List<Map<String, String>> {
        val dpHelper = DatabaseHelper(context)
        return dpHelper.getAllUsers(tableName)
    }

    fun saveUserData(
        context: Context,
        tableName: String,
        firstName: String,
        lastName: String,
        gender: String,
        dob: String
    ) {
        if (firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty() || dob.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = context.getSharedPreferences(tableName, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("first_name", firstName)
            putString("last_name", lastName)
            putString("gender", gender)
            putString("dob", dob)
            apply()
        }

        Toast.makeText(context, "Data saved successfully!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Get saved user data from SharedPreferences
     */
    fun getUserData(context: Context, tableName: String): Map<String, String?> {
        val prefs = context.getSharedPreferences(tableName, Context.MODE_PRIVATE)
        return mapOf(
            "first_name" to prefs.getString("first_name", null),
            "last_name" to prefs.getString("last_name", null),
            "gender" to prefs.getString("gender", null),
            "dob" to prefs.getString("dob", null)
        )
    }

    /**
     * Clear saved data
     */
    fun clearUserData(context: Context, tableName: String) {
        val prefs = context.getSharedPreferences(tableName, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

}
