package com.yogitechnolabs.loginmanager.ui.container

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.yogitechnolabs.loginmanager.R
import com.yogitechnolabs.loginmanager.saloonapp.CrudHelper

class ContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val formContainer = LinearLayout(context).apply {
        orientation = VERTICAL
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            0,
            1f
        )
    }

    private val bottomBar = LinearLayout(context).apply {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        setPadding(24, 16, 24, 24)
    }

    private val manualMap = HashMap<String, Any>()
    private val servicesList = mutableListOf<HashMap<String, Any>>()

    var endpoint = ""
    var signature = ""
    var authToken = ""

    var existingId: String? = null

    var onSuccess: ((response: Any?, layout: ContainerLayout) -> Unit)? = null
    var onError: ((error: Any?) -> Unit)? = null
    var onBeforeSubmit: (() -> Boolean)? = null

    private lateinit var submitButton: MaterialButton

    var showSubmitButton: Boolean = true
        set(value) {
            field = value
            if (::submitButton.isInitialized) {
                submitButton.visibility = if (value) View.VISIBLE else View.GONE
            }
        }

    init {
        orientation = VERTICAL

        // XML attributes read
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.ContainerLayout)
            val txt = a.getString(R.styleable.ContainerLayout_submitText)
            val txtColor = a.getColor(R.styleable.ContainerLayout_submitTextColor, 0xFFFFFFFF.toInt())
            val bgColor = a.getColor(R.styleable.ContainerLayout_submitBackgroundColor, 0xFF6200EE.toInt())
            val radius = a.getDimension(R.styleable.ContainerLayout_submitRadius, 0f)
            showSubmitButton = a.getBoolean(R.styleable.ContainerLayout_showSubmitButton, true)
            a.recycle()

            createSubmitButton()
            txt?.let { submitButton.text = it }
            submitButton.setTextColor(txtColor)
            submitButton.setBackgroundColor(bgColor)
            submitButton.cornerRadius = radius.toInt()
            submitButton.visibility = if (showSubmitButton) View.VISIBLE else View.GONE
        } ?: createSubmitButton()

        addView(formContainer)
        addView(bottomBar)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val children = mutableListOf<View>()
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v != formContainer && v != bottomBar) children.add(v)
        }

        removeAllViews()
        addView(formContainer)
        addView(bottomBar)

        children.forEach { formContainer.addView(it) }
    }

    private fun createSubmitButton() {
        submitButton = MaterialButton(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            text = if (!existingId.isNullOrEmpty()) "Update" else "Save"
            setOnClickListener { submitNow() }
        }
        bottomBar.addView(submitButton)
    }

    // ------------------ New Customizations ------------------

    fun setSubmitButtonStyle(
        bgColor: Int? = null,
        textColor: Int? = null,
        radius: Float? = null,
        padding: Int? = null,
        margin: Int? = null,
        iconRes: Int? = null
    ) {
        bgColor?.let { submitButton.setBackgroundColor(it) }
        textColor?.let { submitButton.setTextColor(it) }
        radius?.let { submitButton.cornerRadius = it.toInt() }
        padding?.let { submitButton.setPadding(it, it, it, it) }

        margin?.let {
            val lp = submitButton.layoutParams as MarginLayoutParams
            lp.setMargins(it, it, it, it)
            submitButton.layoutParams = lp
        }

        iconRes?.let { submitButton.icon = context.getDrawable(it) }
    }

    fun setSubmitButtonText(text: String) { submitButton.text = text }

    fun add(key: String, value: Any): ContainerLayout {
        manualMap[key] = value
        return this
    }

    private fun submitNow() {
        if (onBeforeSubmit?.invoke() == false) return
        if (endpoint.isBlank()) {
            onError?.invoke("Endpoint is empty")
            return
        }

        submitButton.isEnabled = false
        val req = buildRequest()
        val isUpdate = endpoint.matches(Regex(".*/\\w+$"))

        val success: (Any?) -> Unit = {
            submitButton.isEnabled = true
            clearManualData()
            postToMain { onSuccess?.invoke(it, this) }
        }

        val error: (Any?) -> Unit = {
            submitButton.isEnabled = true
            postToMain { onError?.invoke(it) }
        }

        if (isUpdate) CrudHelper.update(endpoint, signature, authToken, req, success, error)
        else CrudHelper.add(endpoint, signature, authToken, req, success, error)
    }

    private fun postToMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block()
        else Handler(Looper.getMainLooper()).post(block)
    }

    private fun buildRequest(): HashMap<String, Any> {
        val finalReq = HashMap<String, Any>()
        finalReq.putAll(collectTextValues(formContainer))
        finalReq.putAll(manualMap)
        if (servicesList.isNotEmpty()) finalReq["services"] = servicesList
        existingId?.let { finalReq["id"] = it }
        return finalReq
    }

    private fun clearManualData() { manualMap.clear(); servicesList.clear() }

    private fun collectTextValues(parent: ViewGroup): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView) {
                val idName = runCatching { resources.getResourceEntryName(v.id) }.getOrNull() ?: ""
                if (idName.startsWith("txt_")) map[idName.substringAfter("_")] = v.text.toString()
            }
            if (v is ViewGroup) map.putAll(collectTextValues(v))
        }
        return map
    }
}