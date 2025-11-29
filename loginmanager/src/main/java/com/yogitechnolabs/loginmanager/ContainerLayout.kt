package com.yogitechnolabs.loginmanager

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class ContainerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL

        // Inflate your internal layout (from dependency)
        LayoutInflater.from(context).inflate(
            R.layout.container_layout,
            this,
            true
        )
    }

    /**
     * PUBLIC FUNCTION → other project will call this:
     * container.buildRequest()
     */
    fun buildRequest(): HashMap<String, Any> {
        return buildRequestFromTextViews(this)
    }

    /**
     * Scan all TextViews and create KEY → VALUE request
     */
    private fun buildRequestFromTextViews(parent: ViewGroup): HashMap<String, Any> {
        val map = HashMap<String, Any>()

        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)

            when (view) {
                is TextView -> {
                    val key = extractKey(view)
                    val value = view.text.toString().trim()
                    map[key] = value
                }

                is ViewGroup -> {
                    map.putAll(buildRequestFromTextViews(view))
                }
            }
        }

        return map
    }

    /**
     * Extract key from ID:
     * txt_name → name
     * tv_email → email
     * edt_mobile → mobile
     */
    private fun extractKey(view: TextView): String {
        return try {
            val fullId = view.resources.getResourceEntryName(view.id)
            fullId.substringAfter("_")     // remove prefix
        } catch (e: Exception) {
            "unknown"
        }
    }

    /**
     * EXTRA FUNCTION (optional):
     * Manually add any request key/value
     *
     * container.addRequestKeyValue("country", "India")
     */
    fun addRequestKeyValue(key: String, value: Any): HashMap<String, Any> {
        val map = buildRequest()
        map[key] = value
        return map
    }
}
