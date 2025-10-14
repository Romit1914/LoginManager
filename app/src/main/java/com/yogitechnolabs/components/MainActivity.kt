package com.yogitechnolabs.components

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yogitechnolabs.components.classes.LoginManager
import com.yogitechnolabs.components.classes.StoryItem
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

        LoginManager.showLoginScreenInActivity(this, binding.root)

        binding.theme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LoginManager.applyTheme("dark")
            } else {
                LoginManager.applyTheme("light")
            }
        }

        binding.loader.setOnClickListener {
            LoginManager.showLoader(this,"please waiting for the response ...")
        }

        LoginManager.showImage(
            this,
            binding.imageString,
            "https://www.adobe.com/acrobat/hub/media_173d13651460eb7e12c0ef4cf8410e0960a20f0ee.jpg"
        )

        LoginManager.showImage(
            this,
            binding.imageDrawable,
            R.drawable.ic_launcher_background
        )

        val stories = listOf(
            StoryItem(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsljLIOrA8cUf5fZ3Y7RQrb2Xbz609Iv5Nfw&s",
                "Story 1"
            ),
            StoryItem(R.drawable.ic_launcher_foreground, "Story 2"),
            StoryItem(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVNN58XFDLxdqtwwWRSE924NjtuSryXFGxjg&s",
                "Story 3",
            ),
            StoryItem(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQsljLIOrA8cUf5fZ3Y7RQrb2Xbz609Iv5Nfw&s",
                "Story 4"
            ),
            StoryItem(R.drawable.ic_launcher_foreground, "Story 5"),
            StoryItem(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRVNN58XFDLxdqtwwWRSE924NjtuSryXFGxjg&s",
                "Story 6"
            )
        )

        LoginManager.loadStories(this,binding.listView,stories,1,false)

        LoginManager.attachForm(
            this,
            binding.saveData,
            binding.etFirstName,
            binding.etLastName,
            binding.etBirthDate,
            binding.radioGroupGender,
            listOf(binding.cbSports,binding.cbMusic,binding.cbDance),
            "ronny"
        )

        val data = LoginManager.getUserData(this, "ronny")
        Log.d("TAG", "data: $data")

        val get = LoginManager.getAllData(this, "ronny")
        Log.d("TAG", "get: $get")

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