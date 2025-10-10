package com.yogitechnolabs.components

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yogitechnolabs.components.classes.LoginManager
import com.yogitechnolabs.components.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        LoginManager.attachForm(this,binding.saveData,binding.etFirstName,binding.etLastName,binding.etGender,binding.etBirthDate,"ronny")

        val data = LoginManager.getUserData(this,"ronny")
        Log.d("TAG","data: $data")

        val get = LoginManager.getAllData(this,"test")
        Log.d("TAG","get: $get")

        binding.clearData.setOnClickListener {
            LoginManager.clearUserData(this,"ronny")
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etpass.text.toString().trim()
            LoginManager.loginWithEmail(email, pass) { success, message ->
                if (success) {
                    Log.d("TAG", "Login Success - $message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("TAG", "Login Failed - $message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}