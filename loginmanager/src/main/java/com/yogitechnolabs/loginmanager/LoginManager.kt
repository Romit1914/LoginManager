package com.yogitechnolabs.loginmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.yogitechnolabs.components.classes.DatabaseHelper

data class StoryItem(
    val image: Any,    // URL (String) या drawable resource (Int)
    val title: String
)
class StoryAdapter(
    private val context: Context,
    private val stories: List<StoryItem>
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.storyImage)
        val textView: TextView = itemView.findViewById(R.id.storyTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_view, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = stories[position]
        Glide.with(context).load(item.image).into(holder.imageView)
        holder.textView.text = item.title
    }

    override fun getItemCount(): Int = stories.size
}
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

    @SuppressLint("MissingInflatedId")
    fun showLoader(context: Context, message: String = "Loading..."): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.progress_loader, null)

        // TextView me message set karna
        val tvMessage = view.findViewById<TextView>(R.id.tvLoaderMessage)
        tvMessage.text = message

        builder.setView(view)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun attachForm(
        context: Context,
        btnSubmit: Button? = null,
        etFirstName: EditText? = null,
        etLastName: EditText? = null,
        etDob: TextView? = null,
        radioGroupGender: RadioGroup? = null, // 👈 RadioGroup for gender
        checkBoxes: List<CheckBox>? = null,
        tableName: String
    ) {
        // 👇 Date Picker for DOB
        etDob?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDob.text = selectedDate
                },
                year, month, day
            )
            datePicker.show()
        }

        // 👇 Button click for saving data
        btnSubmit?.setOnClickListener {
            val firstName = etFirstName?.text.toString().trim()
            val lastName = etLastName?.text.toString().trim()
            val dob = etDob?.text.toString().trim()

            // Get selected gender
            val selectedId = radioGroupGender?.checkedRadioButtonId
            val gender = if (selectedId != -1) {
                val radioButton = radioGroupGender?.findViewById<RadioButton>(selectedId!!)
                radioButton?.text.toString()
            } else {
                ""
            }

            val selectedOptions = checkBoxes
                ?.filter { it.isChecked }
                ?.joinToString(", ") { it.text.toString() }

            // Validate all fields
            if (firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty() || dob.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save data in database
            saveUserDataDB(context, tableName, firstName, lastName, gender, dob,selectedOptions.toString())

            Toast.makeText(context, "Data saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveUserDataDB(context: Context, tableName: String, f: String, l: String, g: String, d: String,selections: String) {
        val dbHelper = DatabaseHelper(context)
        dbHelper.insertUser(tableName, f, l, g, d, selections)
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
        prefs.edit { clear() }
    }

    fun showImage(context: Context, view: View, imageUrl: Any) {
        if (view is ImageView) {
            Glide.with(context)
                .load(imageUrl)
                .into(view)
        } else {
            throw IllegalArgumentException("View must be an ImageView")
        }
    }

    fun loadStories(
        context: Context,
        recyclerView: RecyclerView,
        stories: List<StoryItem>,
        spanCount: Int = 1,
        horizontal: Boolean = false
    ) {
        recyclerView.adapter = StoryAdapter(context, stories)

        recyclerView.layoutManager =
            GridLayoutManager(context, spanCount, if (horizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL, false)

    }

    fun applyTheme(theme: String) {
        when (theme.lowercase()) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    @SuppressLint("MissingInflatedId")
    fun showLoginScreenInActivity(context: Context, rootView: ViewGroup) {
        val inflater = LayoutInflater.from(context)
        val loginView = inflater.inflate(R.layout.login_screen, rootView, false)
        rootView.removeAllViews()
        rootView.addView(loginView)

        val etEmail = loginView.findViewById<EditText>(R.id.etEmail)
        val etPassword = loginView.findViewById<EditText>(R.id.etPassword)
        val btnLogin = loginView.findViewById<Button>(R.id.btnLogin)
        val btnGoogle = loginView.findViewById<ImageView>(R.id.btnGoogle)
        val btnFacebook = loginView.findViewById<ImageView>(R.id.btnFacebook)
        val btnTwitter = loginView.findViewById<ImageView>(R.id.btnTwitter)
        val signup = loginView.findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            loginWithEmail(email, password) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    (context as? Activity)?.finish()
                }
            }
        }

        signup.setOnClickListener {
            showSignupScreenInSameView(context, rootView)
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(context, "Google login clicked", Toast.LENGTH_SHORT).show()
        }

        btnFacebook.setOnClickListener {
            Toast.makeText(context, "Facebook login clicked", Toast.LENGTH_SHORT).show()
        }

        btnTwitter.setOnClickListener {
            Toast.makeText(context, "Twitter login clicked", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingInflatedId")
    fun showSignupScreenInSameView(context: Context, rootView: ViewGroup) {
        val inflater = LayoutInflater.from(context)
        val signupView = inflater.inflate(R.layout.layout_signup_screen, rootView, false)
        rootView.removeAllViews()
        rootView.addView(signupView)

        val name = signupView.findViewById<EditText>(R.id.etName)
        val email = signupView.findViewById<EditText>(R.id.etEmail)
        val password = signupView.findViewById<EditText>(R.id.etPassword)
        val cnPassword = signupView.findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignup = signupView.findViewById<Button>(R.id.btnSignup)
        val loginNow = signupView.findViewById<TextView>(R.id.tvLoginNow)

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{6,}$".toRegex()

        btnSignup.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()
            val cnPasswordText = cnPassword.text.toString()

            if (name.text.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || cnPasswordText.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (!emailText.matches(emailRegex)) {
                Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            } else if (!passwordText.matches(passwordRegex)) {
                Toast.makeText(context, "Password must be at least 6 characters, include 1 uppercase letter, 1 number, and 1 special character", Toast.LENGTH_LONG).show()
            } else if (passwordText != cnPasswordText) {
                Toast.makeText(context, "Password does not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Sign Up successful!", Toast.LENGTH_SHORT).show()
                showLoginScreenInActivity(context, rootView)
            }
        }

        loginNow.setOnClickListener {
            showLoginScreenInActivity(context, rootView)
        }
    }

}
