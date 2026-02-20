package com.yogitechnolabs.loginmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.yogitechnolabs.loginmanager.ui.webDefault.WebDefaultActivity

class PolicyLinkView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val tvPrivacy: TextView
    private val tvTerms: TextView

    private var privacyUrl = ""
    private var termsUrl = ""

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context)
            .inflate(R.layout.view_policy_links, this, true)

        tvPrivacy = findViewById(R.id.tvPrivacy)
        tvTerms = findViewById(R.id.tvTerms)

        tvPrivacy.setOnClickListener {
            openWebView(privacyUrl)
        }

        tvTerms.setOnClickListener {
            openWebView(termsUrl)
        }
    }

    fun setPrivacyUrl(url: String) {
        privacyUrl = url
    }

    fun setTermsUrl(url: String) {
        termsUrl = url
    }

    private fun openWebView(url: String) {
        if (url.isEmpty()) return

        val intent = Intent(context, WebDefaultActivity::class.java)
        intent.putExtra("url", url)
        context.startActivity(intent)
    }
}