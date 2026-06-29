package com.jesus.estresyaparte.mobile.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesus.estresyaparte.mobile.data.network.WebSocketClient
import com.jesus.estresyaparte.shared.BiometricData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var watchdogJob: Job? = null
    private var webSocketClient: WebSocketClient? = null

    init {
        // Inicializamos el cliente de WebSockets (RF-06)
        webSocketClient = WebSocketClient(
            onDataReceived = { biometricData ->
                // Cuando el WebSocket recibe un dato, actualizamos la UI
                onNewDataReceived(biometricData)
            },
            onError = { errorMessage ->
                _uiState.update { it.copy(errorMessage = errorMessage) }
            }
        )
        
        // Conexión a la URL de tu servidor de WebSocket en la Nube (dirección ejemplo)
        webSocketClient?.connect("ws://tu-servidor-cloud.com/salud-laboral")
    }

    fun onNewDataReceived(data: BiometricData) {
        _uiState.update { currentState ->
            currentState.copy(
                latestData = data,
                isLive = true, // Indica que los datos son "En Vivo" (RF-07)
                showOutdatedWarning = false,
                errorMessage = null
            )
        }
        startWatchdogTimer()
    }

    private fun startWatchdogTimer() {
        watchdogJob?.cancel()
        watchdogJob = viewModelScope.launch {
            delay(60000) // RNF-03: Si en 60 segundos no hay datos nuevos, activa la alerta
            
            _uiState.update { currentState ->
                currentState.copy(
                    isLive = false, // Cambia el estado a "Datos Históricos" (RF-07)
                    showOutdatedWarning = true // Muestra el banner informativo
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        watchdogJob?.cancel()
        webSocketClient?.disconnect() // Evitar fugas de memoria al cerrar la pantalla
    }
}
