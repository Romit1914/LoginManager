package com.yogitechnolabs.loginmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager as FbLoginManager
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.github.scribejava.core.model.OAuth1RequestToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONException
import androidx.core.net.toUri
import com.github.scribejava.apis.TwitterApi
import com.google.gson.Gson
import com.yogitechnolabs.components.classes.DatabaseHelper
import com.yogitechnolabs.loginmanager.api.RetrofitClient
import com.yogitechnolabs.loginmanager.api.SignupRequest
import com.yogitechnolabs.loginmanager.api.SignupResponse

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
    private var twitterApiKey: String? = null
    private var twitterApiSecret: String? = null
    private var twitterCallbackUrl: String? = null
    private var twitterRequestToken: OAuth1RequestToken? = null
    private var twitterCallback: ((success: Boolean, username: String?, userId: String?) -> Unit)? = null

    private var fbCallbackManager: CallbackManager? = null
    private var fbLoginCallback: ((success: Boolean, name: String?, email: String?) -> Unit)? = null

    @SuppressLint("StaticFieldLeak")
    private var googleSignInClient: GoogleSignInClient? = null
    private var googleCallback: ((success: Boolean, message: String) -> Unit)? = null
    const val GOOGLE_SIGN_IN_REQUEST = 1001

    fun setupGoogleLogin(activity: Activity, clientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun getGoogleSignInIntent(): Intent? {
        return googleSignInClient?.signInIntent
    }

    fun handleGoogleResult(data: Intent?, onResult: (Boolean, String) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val name = account.displayName ?: "User"
            val email = account.email ?: "N/A"
            val token = account.idToken ?: ""
            Log.d("LoginManager", "Google: name=$name, email=$email, token=$token")
            onResult(true, "Welcome $name ($email)")
        } catch (e: ApiException) {
            onResult(false, "Google Sign-In failed: ${e.message}")
        }
    }

    fun logoutFromGoogle(
        activity: Activity,
        onLogout: (success: Boolean, message: String) -> Unit
    ) {
        if (googleSignInClient == null) {
            onLogout(false, "Google client not initialized")
            return
        }

        googleSignInClient?.signOut()
            ?.addOnCompleteListener(activity) {
                onLogout(true, "Logged out successfully")
            }
            ?.addOnFailureListener {
                onLogout(false, "Logout failed: ${it.message}")
            }
    }


    fun setupFacebookLogin(context: Context) {
        FacebookSdk.sdkInitialize(context.applicationContext)
        fbCallbackManager = CallbackManager.Factory.create()
    }

    /**
     * Call on Facebook button click
     */
    fun startFacebookSignIn(activity: Activity, callback: (success: Boolean, name: String?, email: String?) -> Unit) {
        fbLoginCallback = callback
        fbCallbackManager ?: run { setupFacebookLogin(activity) }

        FbLoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
        FbLoginManager.getInstance().registerCallback(fbCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val request = GraphRequest.newMeRequest(result.accessToken) { obj, _ ->
                    try {
                        val name = obj?.getString("name")
                        val email = obj?.getString("email")
                        fbLoginCallback?.invoke(true, name, email)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        fbLoginCallback?.invoke(false, null, null)
                    }
                }
                val params = Bundle()
                params.putString("fields", "id,name,email")
                request.parameters = params
                request.executeAsync()
            }

            override fun onCancel() {
                fbLoginCallback?.invoke(false, null, null)
            }

            override fun onError(error: FacebookException) {
                fbLoginCallback?.invoke(false, null, null)
            }
        })
    }

    /**
     * Call inside Activity's onActivityResult
     */
    fun handleFacebookResult(requestCode: Int, resultCode: Int, data: Intent?) {
        fbCallbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    fun setupTwitterLogin(apiKey: String, apiSecret: String, callbackUrl: String) {
        twitterApiKey = apiKey
        twitterApiSecret = apiSecret
        twitterCallbackUrl = callbackUrl
    }

    fun startTwitterSignIn(activity: Activity, callback: (success: Boolean, username: String?, userId: String?) -> Unit) {
        twitterCallback = callback

        if (twitterApiKey.isNullOrEmpty() || twitterApiSecret.isNullOrEmpty() || twitterCallbackUrl.isNullOrEmpty()) {
            callback(false, null, null)
            return
        }

        val service = TwitterApi.instance()
            .createService(twitterApiKey, twitterApiSecret,twitterCallbackUrl,null, System.out,"1.0",null,null)

        Thread {
            try {
                twitterRequestToken = service.requestToken
                val authUrl = service.getAuthorizationUrl(twitterRequestToken)

                activity.runOnUiThread {
                    val intent = Intent(Intent.ACTION_VIEW, authUrl.toUri())
                    activity.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                activity.runOnUiThread { callback(false, null, null) }
            }
        }.start()
    }

    fun handleTwitterCallback(intent: Intent) {
        val uri = intent.data ?: return
        if (uri.toString().startsWith(twitterCallbackUrl!!)) {
            val verifier = uri.getQueryParameter("oauth_verifier") ?: return
            val service = TwitterApi.instance()
                .createService(twitterApiKey, twitterApiSecret,twitterCallbackUrl,null, System.out,"1.0",null,null)
            Thread {
                try {
                    val accessToken = service.getAccessToken(twitterRequestToken, verifier)
                    val username = accessToken.token // API call real username fetch
                    val userId = accessToken.tokenSecret
                    twitterCallback?.invoke(true, username, userId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    twitterCallback?.invoke(false, null, null)
                }
            }.start()
        }
    }

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

    fun signupUser(
        context: Context,
        name: String,
        email: String,
        password: String,
        phone: String,
        role: String = "User",
        callback: (Boolean, String, SignupResponse?) -> Unit
    ) {
        // Login ke liye sirf email + password bhejna hai
        val request = mapOf(
            "name" to name,
            "email" to email,
            "password" to password,
            "phone" to phone,
            "role" to role
        )

        // STATIC SIGNATURE (as per your requirement)
        val signature = "d3bfa8b9b834a6497dd8fc0fcfed9f695e17688b1a2b3297d788755e796216bf"

        RetrofitClient.api.loginUser(signature, request)
            .enqueue(object : retrofit2.Callback<SignupResponse> {
                override fun onResponse(
                    call: retrofit2.Call<SignupResponse>,
                    response: retrofit2.Response<SignupResponse>
                ) {
                    Log.d("API_RESPONSE", "Code: ${response.code()}")
                    Log.d("API_RESPONSE", "Raw: ${response.raw()}")

                    val body = response.body()
                    Log.d("API_RESPONSE", "Body: $body")

                    // SUCCESS (body available)
                    if (response.isSuccessful && body != null) {
                        callback(true, "Success", body)
                    }
                    else {
                        // ERROR BODY ALWAYS RETURN KARO
                        val errorJson = response.errorBody()?.string()
                        Log.e("API_RESPONSE", "Error Body: $errorJson")

                        // Email exist ho ya password wrong ho → JSON mil jata hai
                        // isliye null NAHI bhejenge → error parse karke response object bana denge.
                        val parsed = try {
                            Gson().fromJson(errorJson, SignupResponse::class.java)
                        } catch (e: Exception) {
                            null
                        }

                        callback(false, "Failed", parsed)
                    }
                }

                override fun onFailure(call: retrofit2.Call<SignupResponse>, t: Throwable) {
                    Log.e("API_RESPONSE", "Failure: ${t.localizedMessage}")
                    callback(false, "Network Error", null)
                }
            })
    }

    fun loginUser(
        context: Context,
        email: String,
        password: String,
        callback: (Boolean, String, SignupResponse?) -> Unit
    ) {
        val loginBody = mapOf(
            "email" to email,
            "password" to password
        )

        val signature =
            "d3bfa8b9b834a6497dd8fc0fcfed9f695e17688b1a2b3297d788755e796216bf"

        Log.d("API_LOGIN", "REQUEST BODY → $loginBody")

        RetrofitClient.api.loginUser(signature, loginBody)
            .enqueue(object : retrofit2.Callback<SignupResponse> {

                override fun onResponse(
                    call: retrofit2.Call<SignupResponse>,
                    response: retrofit2.Response<SignupResponse>
                ) {

                    Log.d("API_LOGIN", "CODE → ${response.code()}")
                    Log.d("API_LOGIN", "RAW → ${response.raw()}")

                    val body = response.body()
                    Log.d("API_LOGIN", "BODY → $body")

                    val error = response.errorBody()?.string()
                    Log.e("API_LOGIN", "ERROR BODY → $error")

                    if (response.isSuccessful && body != null) {
                        callback(true, "Login Successful", body)
                    } else {
                        callback(false, "Login Failed", null)
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<SignupResponse>,
                    t: Throwable
                ) {
                    Log.e("API_LOGIN", "FAILURE → ${t.localizedMessage}")
                    callback(false, "Network Error: ${t.message}", null)
                }
            })
    }



    private fun saveUserToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        prefs.edit {
            putString("token", token)
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
    fun showLoginScreenInActivity(context: Context, rootView: ViewGroup , clientID: String,googleLauncher: ActivityResultLauncher<Intent>? = null) {
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
        val logout = loginView.findViewById<Button>(R.id.logout)

        logout.setOnClickListener {
            logoutFromGoogle(context as Activity){ success, message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            loginUser(
                context,
                email = email,
                password = password
            ) { success, msg, response ->

                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

                if (success && response != null) {
                    Log.d("API_RESPONSE", "Name: ${response.name}")
                    Log.d("API_RESPONSE", "Email: ${response.email}")
                    Log.d("API_RESPONSE", "Token: ${response.token}")
                } else {
                    Log.e("API_RESPONSE", "Login Failed or No Response")
                }
            }
        }


        signup.setOnClickListener {
            showSignupScreenInSameView(context, rootView)
        }

        btnGoogle.setOnClickListener {
            setupGoogleLogin(context as Activity, clientID)
            getGoogleSignInIntent()?.let { intent ->
                if (googleLauncher != null) {
                    googleLauncher.launch(intent) // ✅ Use modern launcher
                } else {
                    context.startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST) // fallback
                }
            }
        }

        btnFacebook.setOnClickListener {
            startFacebookSignIn(context as Activity) { success, name, email ->
                if (success) {
                    Toast.makeText(context, "Welcome $name ($email)", Toast.LENGTH_SHORT).show()
                    context.finish()
                } else {
                    Toast.makeText(context, "Facebook login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnTwitter.setOnClickListener {
            setupTwitterLogin(
                apiKey = "123456789012345",
                apiSecret = "123456789012345",
                callbackUrl = "myapp://twitter-callback"
            )

            startTwitterSignIn(context as Activity) { success, username, userId ->
                if (success) {
                    Toast.makeText(context, "Welcome $username", Toast.LENGTH_SHORT).show()
                    context.finish()
                } else {
                    Toast.makeText(context, "Twitter login failed", Toast.LENGTH_SHORT).show()
                }
            }
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
                showLoginScreenInActivity(context, rootView,"CLIENT_ID")
            }
        }

        loginNow.setOnClickListener {
            showLoginScreenInActivity(context, rootView,"CLIENT_ID")
        }
    }

}
