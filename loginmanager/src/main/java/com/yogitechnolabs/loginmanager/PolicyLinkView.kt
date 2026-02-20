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
        if (attrs == null) {
            orientation = HORIZONTAL
            return
        }

        val ta = context.obtainStyledAttributes(attrs, R.styleable.PolicyLinkView)

        val privacyText = ta.getString(R.styleable.PolicyLinkView_privacyText)
        val termsText = ta.getString(R.styleable.PolicyLinkView_termsText)

        val color = ta.getColor(
            R.styleable.PolicyLinkView_linkTextColor,
            Color.BLUE
        )

        val sizePx = ta.getDimension(
            R.styleable.PolicyLinkView_linkTextSize,
            14f * resources.displayMetrics.scaledDensity
        )

        val policyOrientation = ta.getInt(
            R.styleable.PolicyLinkView_policyOrientation,
            0
        )

        // apply text
        privacyText?.let { tvPrivacy.text = it }
        termsText?.let { tvTerms.text = it }

        // apply color & size
        tvPrivacy.setTextColor(color)
        tvTerms.setTextColor(color)
        tvDivider.setTextColor(color)

        val sizeSp = pxToSp(sizePx)
        tvPrivacy.textSize = sizeSp
        tvTerms.textSize = sizeSp
        tvDivider.textSize = sizeSp

        // apply orientation
        if (policyOrientation == 1) {
            orientation = VERTICAL
            tvDivider.visibility = GONE
        } else {
            orientation = HORIZONTAL
            tvDivider.visibility = VISIBLE
        }

        ta.recycle()
    }

    /* -------- Runtime setters -------- */

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

    private fun pxToSp(px: Float): Float {
        return px / resources.displayMetrics.scaledDensity
    }
}