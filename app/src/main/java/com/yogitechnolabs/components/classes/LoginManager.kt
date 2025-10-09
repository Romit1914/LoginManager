package com.yogitechnolabs.components.classes


object LoginManager {

    // --------- Email/Password Login ---------
    fun loginWithEmail(
        email: String,
        password: String,
        callback: (success: Boolean, message: String) -> Unit
    ) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        when {
            email.isEmpty() || password.isEmpty() -> {
                callback(false, "Email or password cannot be empty")
            }
            !email.matches(emailRegex) -> {
                callback(false, "Invalid email format")
            }
            else -> {
                callback(true, "Login successful")
            }
        }
    }


}
