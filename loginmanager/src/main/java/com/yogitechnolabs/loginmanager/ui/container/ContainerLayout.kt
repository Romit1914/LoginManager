package com.yogitechnolabs.loginmanager.ui.container

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.yogitechnolabs.loginmanager.saloonapp.CrudHelper

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
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            visibility = if (showSubmitButton) View.VISIBLE else View.GONE
            text = if (!existingId.isNullOrEmpty()) "Update" else "Save"
            setOnClickListener { submitNow() }
        }
        addView(submitButton)
    }

    fun add(key: String, value: Any): ContainerLayout {
        manualMap[key] = value
        return this
    }

    private fun submitNow() {
        val req = build()

        if (existingId != null) {
            // UPDATE
            CrudHelper.update(
                endpoint = "$endpoint/$existingId",
                signature = signature,
                authToken = authToken,
                data = req,
                onSuccess = { res ->
                    postToMain { onSuccess?.invoke(res, this) }
                },
                onError = { err ->
                    postToMain { onError?.invoke(err) }
                }
            )
        } else {
            // ADD
            CrudHelper.add(
                endpoint = endpoint,
                signature = signature,
                authToken = authToken,
                data = req,
                onSuccess = { res ->
                    postToMain { onSuccess?.invoke(res, this) }
                },
                onError = { err ->
                    postToMain { onError?.invoke(err) }
                }
            )
        }
    }

    // helper function to run callbacks on main thread
    private fun postToMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block()
        else Handler(Looper.getMainLooper()).post { block() }
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