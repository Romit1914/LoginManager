package com.yogitechnolabs.loginmanager.saloonapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.databinding.ActivitySalonBinding

class SalonActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalonBinding
    private val viewModel: SalonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySalonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ownerId = intent.getStringExtra("ownerId") ?: ""

        val adapter = EmployeeAdapter()
        binding.recyclerEmployees.layoutManager = LinearLayoutManager(this)
        binding.recyclerEmployees.adapter = adapter

        viewModel.ownerLive.observe(this) {
            adapter.setData(it.employees)
        }

        viewModel.loadOwner(ownerId)
    }
}