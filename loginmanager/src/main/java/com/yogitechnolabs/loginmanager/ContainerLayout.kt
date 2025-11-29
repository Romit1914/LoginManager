package com.yogitechnolabs.loginmanager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Reusable Container Layout
 * - Inflate normal XML layout
 * - Build request automatically from TextViews
 * - Or manually add key-value pairs
 */
class ContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    // Internal request map for manual additions
    private val manualRequestMap = HashMap<String, Any>()

    init {
        orientation = VERTICAL
        // Inflate the XML layout inside this container
        LayoutInflater.from(context).inflate(R.layout.container_layout, this, true)
    }

    /**
     * Build request automatically from child TextViews
     */
    fun buildRequestFromTextViews(): HashMap<String, Any> {
        return collectChildViews(this)
    }

    /**
     * Add key-value manually
     */
    fun addRequestKeyValue(key: String, value: Any): ContainerLayout {
        manualRequestMap[key] = value
        return this // for chaining
    }

    /**
     * Get final request map including manual additions
     */
    fun buildRequest(): HashMap<String, Any> {
        val autoMap = collectChildViews(this)
        autoMap.putAll(manualRequestMap) // manual values override auto if key same
        return autoMap
    }

    /**
     * Recursive function to scan all child views
     */
    private fun collectChildViews(container: ViewGroup): HashMap<String, Any> {
        val request = HashMap<String, Any>()

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)

            if (view is TextView) {
                val idName = view.resources.getResourceEntryName(view.id)
                val key = idName.substringAfter("_") // txt_name → name
                val value = view.text.toString().trim()
                request[key] = value
            }

            if (view is ViewGroup) {
                request.putAll(collectChildViews(view))
            }
        }

        return request
    }
}
