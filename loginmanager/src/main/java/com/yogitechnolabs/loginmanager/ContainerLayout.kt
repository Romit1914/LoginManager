package com.yogitechnolabs.loginmanager

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

    init {
        orientation = VERTICAL
    }

    // -------- PUBLIC FUNCTIONS -------- //

    fun add(key: String, value: Any): ContainerLayout {
        manualMap[key] = value
        return this
    }

    fun build(): HashMap<String, Any> {
        val finalReq = HashMap<String, Any>()

        // 1) Auto TextView read
        finalReq.putAll(collectTextValues(this))

        // 2) Manual added values
        finalReq.putAll(manualMap)

        return finalReq
    }

    // -------- INTERNAL FUNCTIONS -------- //

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

                val key = idName.substringAfter("_", idName)
                map[key] = v.text.toString()
            }

            if (v is ViewGroup) {
                map.putAll(collectTextValues(v))
            }
        }

        return map
    }
}
