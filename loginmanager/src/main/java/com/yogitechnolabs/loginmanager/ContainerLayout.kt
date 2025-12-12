package com.yogitechnolabs.loginmanager

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.yogitechnolabs.loginmanager.saloonapp.CrudHelper

class ContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val manualMap = HashMap<String, Any>()
    private val servicesList = mutableListOf<HashMap<String, Any>>()

    var endpoint: String = ""
    var signature: String = ""
    var authToken: String = ""
    private var parentDialog: AlertDialog? = null

    init {
        orientation = VERTICAL
        addSubmitButton()
    }

    private fun addSubmitButton() {
        val btn = Button(context).apply {
            text = "Save"
            setOnClickListener { submitNow() }
        }
        addView(btn)
    }

    fun setParentDialog(dialog: AlertDialog) {
        parentDialog = dialog
    }

    fun add(key: String, value: Any): ContainerLayout {
        manualMap[key] = value
        return this
    }

    fun addService(serviceName: String, price: Int): ContainerLayout {
        servicesList.add(hashMapOf("serviceName" to serviceName, "price" to price))
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
                parentDialog?.dismiss()
            },
            onError = { error ->
                Log.e("ContainerLayout", "ERROR → $error")
            }
        )
    }

    fun build(): HashMap<String, Any> {
        val finalReq = HashMap<String, Any>()
        finalReq.putAll(collectTextValues(this))
        finalReq.putAll(manualMap)

        if (servicesList.isNotEmpty())
            finalReq["services"] = servicesList

        finalReq["id"] = System.currentTimeMillis()

        return finalReq
    }

    private fun collectTextValues(parent: ViewGroup): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView) {
                val idName = try { resources.getResourceEntryName(v.id) } catch (e: Exception) { "" }
                if (idName.startsWith("txt_")) {
                    val key = idName.substringAfter("_")
                    map[key] = v.text.toString()
                }
            }
            if (v is ViewGroup) map.putAll(collectTextValues(v))
        }
        return map
    }
}
