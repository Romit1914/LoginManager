package com.yogitechnolabs.loginmanager.saloonapp.model

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

data class ResponseModel<T>(
    val success: Boolean,
    val message: String,
    val data: T?
) {

    companion object {

        inline fun <reified T> listType(): java.lang.reflect.Type {
            return object : TypeToken<ResponseModel<List<T>>>() {}.type
        }

        inline fun <reified T> singleType(): java.lang.reflect.Type {
            return object : TypeToken<ResponseModel<T>>() {}.type
        }
    }
}
