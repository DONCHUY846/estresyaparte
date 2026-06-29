package com.jesus.estresyaparte.mobile.presentation.dashboard

import com.jesus.estresyaparte.shared.BiometricData

/**
 * Representa los estados posibles de la pantalla del celular (RF-07).
 */
data class DashboardUiState(
    // El último dato biométrico recibido del servidor
    val latestData: BiometricData? = null,
    
    // Indica si la información es "En Vivo" o "Datos Históricos" (RF-07)
    val isLive: Boolean = false,
    
    // Dispara el banner de advertencia si pasa más de 1 minuto sin datos frescos (RNF-03 / RF-07)
    val showOutdatedWarning: Boolean = false,
    
    // Mensaje de error en caso de fallo de conexión de red general
    val errorMessage: String? = null
)
