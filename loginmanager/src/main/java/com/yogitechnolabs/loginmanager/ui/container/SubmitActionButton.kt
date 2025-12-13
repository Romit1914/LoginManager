package com.yogitechnolabs.loginmanager.ui.container

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Button

import com.yogitechnolabs.loginmanager.saloonapp.CrudHelper

class SubmitActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatButton(context, attrs) {

    var endpoint = ""
    var signature = ""
    var authToken = ""

    var containerLayout: ContainerLayout? = null

    var onSuccess: ((Any?) -> Unit)? = null
    var onError: ((Any?) -> Unit)? = null

    init {
        text = "Save"
        setOnClickListener { submit() }
    }

    private fun submit() {
        val req = containerLayout?.build() ?: return
        Log.d("SubmitButton", "REQUEST → $req")

        CrudHelper.add(
            endpoint = endpoint,
            signature = signature,
            authToken = authToken,
            data = req,
            onSuccess = {
                Log.d("SubmitButton", "SUCCESS → $it")
                onSuccess?.invoke(it)
            },
            onError = {
                Log.e("SubmitButton", "ERROR → $it")
                onError?.invoke(it)
            }
        )
    }
}
