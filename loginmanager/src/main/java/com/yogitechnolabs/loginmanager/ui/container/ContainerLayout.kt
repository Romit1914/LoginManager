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

    fun setSubmitButtonText(text: String) {
        submitButton?.text = text
    }

    // for update case
    var existingId: String? = null

    var onSuccess: ((response: Any?, layout: ContainerLayout) -> Unit)? = null
    var onError: ((error: Any?) -> Unit)? = null

    // ✅ Fragment validation hook
    var onBeforeSubmit: (() -> Boolean)? = null

    init {
        orientation = VERTICAL

        formContainer.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            0,
            1f
        )

        addView(formContainer)
        addSubmitButton()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        orientation = VERTICAL

        val children = mutableListOf<View>()
        for (i in 0 until childCount) {
            children.add(getChildAt(i))
        }

        removeAllViews()

        // 🔥 IMPORTANT: weight dobara set
        formContainer.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            0,
            1f
        )

        addView(formContainer)

        children.forEach {
            if (it !== formContainer) {
                formContainer.addView(it)
            }
        }

        addSubmitButton()
    }

    private fun addSubmitButton() {
        if (submitButton != null) return
        submitButton = Button(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER

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

        // ✅ validation from Fragment
        if (onBeforeSubmit?.invoke() == false) return

        if (endpoint.isBlank()) {
            onError?.invoke("Endpoint is empty")
            return
        }

        submitButton?.isEnabled = false   // prevent double click

        val req = buildRequest()

        val isUpdate = endpoint.matches(Regex(".*/\\w+$"))

        if (isUpdate) {
            CrudHelper.update(
                endpoint = endpoint,
                signature = signature,
                authToken = authToken,
                data = req,
                onSuccess = { res ->
                    clearManualData()
                    submitButton?.isEnabled = true
                    postToMain { onSuccess?.invoke(res, this) }
                },
                onError = { err ->
                    submitButton?.isEnabled = true
                    postToMain { onError?.invoke(err) }
                }
            )
        } else {
            CrudHelper.add(
                endpoint = endpoint,
                signature = signature,
                authToken = authToken,
                data = req,
                onSuccess = { res ->
                    clearManualData()
                    submitButton?.isEnabled = true
                    postToMain { onSuccess?.invoke(res, this) }
                },
                onError = { err ->
                    submitButton?.isEnabled = true
                    postToMain { onError?.invoke(err) }
                }
            )
        }
    }

    private fun postToMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block()
        else Handler(Looper.getMainLooper()).post { block() }
    }

    // 🔒 build is PRIVATE now
    private fun buildRequest(): HashMap<String, Any> {
        val finalReq = HashMap<String, Any>()
        finalReq.putAll(collectTextValues(formContainer))
        finalReq.putAll(manualMap)

        if (servicesList.isNotEmpty()) {
            finalReq["services"] = servicesList
        }

        // ✅ only update sends ID
        if (!existingId.isNullOrEmpty()) {
            finalReq["id"] = existingId!!
        }

        return finalReq
    }

    private fun clearManualData() {
        manualMap.clear()
        servicesList.clear()
    }

    private fun collectTextValues(parent: ViewGroup): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView) {
                val idName = try {
                    resources.getResourceEntryName(v.id)
                } catch (e: Exception) {
                    ""
                }
                if (idName.startsWith("txt_")) {
                    map[idName.substringAfter("_")] = v.text.toString()
                }
            }
            if (v is ViewGroup) {
                map.putAll(collectTextValues(v))
            }
        }
        return map
    }
}