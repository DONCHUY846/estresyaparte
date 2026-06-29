package com.jesus.estresyaparte.wear.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(context: Context) {

    private val connectivityManager = 
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isWifiConnected = MutableStateFlow(false)
    val isWifiConnected: StateFlow<Boolean> = _isWifiConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // El dispositivo se conectó a una red
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            val hasWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            if (hasWifi) {
                _isWifiConnected.value = true
            }
        }

        override fun onLost(network: Network) {
            // Se perdió la conexión
            _isWifiConnected.value = false
        }
    }

    fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) // Nos interesa específicamente el Wi-Fi (RF-03)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        // Verificación inicial de seguridad
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        _isWifiConnected.value = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    fun stopMonitoring() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
