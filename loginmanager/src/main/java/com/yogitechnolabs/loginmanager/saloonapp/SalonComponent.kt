package com.yogitechnolabs.loginmanager.saloonapp

import android.content.Context
import android.content.Intent
import com.yogitechnolabs.loginmanager.saloonapp.ui.SalonActivity

object SalonComponent {

    fun show(context: Context, ownerId: String) {
        val intent = Intent(context, SalonActivity::class.java)
        intent.putExtra("ownerId", ownerId)
        context.startActivity(intent)
    }
}