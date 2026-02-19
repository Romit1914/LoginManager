package com.yogitechnolabs.loginmanager.ui

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresPermission

class NetworkMonitor(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var onAvailable: (() -> Unit)? = null
    private var onLost: (() -> Unit)? = null

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun register(
        onAvailable: () -> Unit,
        onLost: () -> Unit
    ) {
        this.onAvailable = onAvailable
        this.onLost = onLost

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            onAvailable?.invoke()
        }

        override fun onLost(network: Network) {
            onLost?.invoke()
        }
    }

    companion object {
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun isInternetAvailable(context: Context): Boolean {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork ?: return false
                val cap = cm.getNetworkCapabilities(network) ?: return false
                return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val info = cm.activeNetworkInfo
                return info != null && info.isConnected
            }
        }
    }
}
