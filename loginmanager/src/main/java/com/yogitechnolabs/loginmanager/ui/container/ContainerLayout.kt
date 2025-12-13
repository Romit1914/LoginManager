package com.yogitechnolabs.loginmanager.ui.container

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.yogitechnolabs.loginmanager.saloonapp.CrudHelper
import com.yogitechnolabs.loginmanager.saloonapp.model.Service

class ContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val formContainer = LinearLayout(context).apply {
        orientation = VERTICAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f)
    }

    private val manualMap = HashMap<String, Any>()
    private val servicesList = mutableListOf<HashMap<String, Any>>()

    var endpoint: String = ""
    var signature: String = ""
    var authToken: String = ""

    private var submitButton: Button? = null

    var showSubmitButton: Boolean = true
        set(value) {
            field = value
            submitButton?.visibility = if (value) View.VISIBLE else View.GONE
        }

    // New: dynamic button text
    fun setSubmitButtonText(text: String) {
        submitButton?.text = text
    }

    // New: optional existing ID (for update)
    var existingId: String? = null

    var onSuccess: ((response: Any?, layout: ContainerLayout) -> Unit)? = null
    var onError: ((error: Any?) -> Unit)? = null

    init {
        orientation = VERTICAL
        addView(formContainer)
        addSubmitButton()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val children = mutableListOf<View>()
        for (i in 0 until childCount) children.add(getChildAt(i))
        removeAllViews()
        addView(formContainer)
        children.forEach { if (it !== formContainer) formContainer.addView(it) }
        addSubmitButton()
    }

    private fun addSubmitButton() {
        if (submitButton != null) return
        submitButton = Button(context).apply {
            text = "Save"
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            visibility = if (showSubmitButton) View.VISIBLE else View.GONE
            setOnClickListener { submitNow() }
        }
        addView(submitButton)
    }

    fun add(key: String, value: Any): ContainerLayout {
        manualMap[key] = value
        return this
    }

    fun setServices(services: List<Service>): ContainerLayout {
        servicesList.clear()
        services.forEach {
            servicesList.add(hashMapOf(
                "id" to (it.id ?: ""),
                "serviceName" to (it.name ?: ""),
                "price" to (it.custom_price?.toIntOrNull() ?: it.base_price?.toIntOrNull() ?: 0)
            ))
        }
        return this
    }

    private fun submitNow() {
        val req = build()
        Log.d("ContainerLayout", "REQUEST → $req")

        CrudHelper.add(
            endpoint = endpoint,
            signature = signature,
            authToken = authToken,
            data = req,
            onSuccess = { response ->
                Log.d("ContainerLayout", "SUCCESS → $response")
                onSuccess?.invoke(response, this)
            },
            onError = { error ->
                Log.e("ContainerLayout", "ERROR → $error")
                onError?.invoke(error)
            }
        )
    }

    fun build(): HashMap<String, Any> {
        val finalReq = HashMap<String, Any>()
        finalReq.putAll(collectTextValues(formContainer))
        finalReq.putAll(manualMap)

        if (servicesList.isNotEmpty())
            finalReq["services"] = servicesList

        // Update case: use existing ID
        finalReq["id"] = existingId ?: System.currentTimeMillis().toString()
        return finalReq
    }

    private fun collectTextValues(parent: ViewGroup): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView) {
                val idName = try { resources.getResourceEntryName(v.id) } catch (e: Exception) { "" }
                if (idName.startsWith("txt_")) map[idName.substringAfter("_")] = v.text.toString()
            }
            if (v is ViewGroup) map.putAll(collectTextValues(v))
        }
        return map
    }
}