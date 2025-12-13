package com.yogitechnolabs.loginmanager.ui.container

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class ContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val manualMap = HashMap<String, Any>()
    private val servicesList = mutableListOf<HashMap<String, Any>>()

    init {
        orientation = VERTICAL
    }

    fun add(key: String, value: Any): ContainerLayout {
        manualMap[key] = value
        return this
    }

    fun addService(serviceName: String, price: Int): ContainerLayout {
        servicesList.add(hashMapOf("serviceName" to serviceName, "price" to price))
        return this
    }

    fun build(): HashMap<String, Any> {
        val req = HashMap<String, Any>()
        req.putAll(collectTextValues(this))
        req.putAll(manualMap)

        if (servicesList.isNotEmpty())
            req["services"] = servicesList

        req["id"] = System.currentTimeMillis()
        return req
    }

    private fun collectTextValues(parent: ViewGroup): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        for (i in 0 until parent.childCount) {
            val v = parent.getChildAt(i)
            if (v is TextView) {
                val idName = try { resources.getResourceEntryName(v.id) } catch (_: Exception) { "" }
                if (idName.startsWith("txt_")) {
                    map[idName.substringAfter("_")] = v.text.toString()
                }
            }
            if (v is ViewGroup) map.putAll(collectTextValues(v))
        }
        return map
    }
}