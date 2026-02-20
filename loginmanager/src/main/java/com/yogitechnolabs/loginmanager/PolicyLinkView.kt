package com.yogitechnolabs.loginmanager

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
    private val tvDivider: TextView

    private var privacyUrl = ""
    private var termsUrl = ""

    init {
        orientation = HORIZONTAL // default

        LayoutInflater.from(context)
            .inflate(R.layout.view_policy_links, this, true)

        tvPrivacy = findViewById(R.id.tvPrivacy)
        tvTerms = findViewById(R.id.tvTerms)
        tvDivider = findViewById(R.id.tvDivider)

        readAttributes(attrs)

        tvPrivacy.setOnClickListener { openWebView(privacyUrl) }
        tvTerms.setOnClickListener { openWebView(termsUrl) }
    }

    private fun readAttributes(attrs: AttributeSet?) {
        if (attrs == null) return

        val ta = context.obtainStyledAttributes(attrs, R.styleable.PolicyLinkView)

        val color = ta.getColor(
            R.styleable.PolicyLinkView_linkTextColor,
            Color.BLUE
        )

        val policyOrientation = ta.getInt(
            R.styleable.PolicyLinkView_policyOrientation,
            0
        )

        tvPrivacy.setTextColor(color)
        tvTerms.setTextColor(color)
        tvDivider.setTextColor(color)

        if (policyOrientation == 1) {
            setOrientationVertical()
        } else {
            setOrientationHorizontal()
        }

        ta.recycle()
    }

    fun setPrivacyUrl(url: String) {
        privacyUrl = url
    }

    fun setTermsUrl(url: String) {
        termsUrl = url
    }

    fun setOrientationVertical() {
        orientation = VERTICAL
        tvDivider.visibility = GONE
    }

    fun setOrientationHorizontal() {
        orientation = HORIZONTAL
        tvDivider.visibility = VISIBLE
    }

    private fun openWebView(url: String) {
        if (url.isEmpty()) return
        val intent = Intent(context, WebDefaultActivity::class.java)
        intent.putExtra("url", url)
        context.startActivity(intent)
    }
}