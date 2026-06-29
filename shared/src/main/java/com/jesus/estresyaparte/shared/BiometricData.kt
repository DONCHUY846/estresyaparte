package com.jesus.estresyaparte.shared

/**
 * Representa el paquete de datos biométricos y ambientales capturados por el Smartwatch.
 * Este modelo es compartido tanto por el módulo de Wear OS (para captura y buffer) 
 * como por el módulo Mobile (para visualización en el Dashboard).
 */
data class BiometricData(
    // Identificador único para el registro (esencial para la base de datos local Room)
    val id: String = java.util.UUID.randomUUID().toString(),
    
    // Identificador del usuario que porta el dispositivo (RF-01)
    val userId: String,
    
    // Pulsaciones por minuto (RF-03)
    val heartRate: Int,
    
    // Magnitud del movimiento calculada a partir del acelerómetro (RF-04)
    // Nos servirá para identificar si el usuario está en reposo
    val IsMoving: Boolean,
    
    // Nivel de luz ambiental en luxes (RF-05 para Fatiga Visual)
    val ambientLight: Float,
    
    // Momento exacto de la captura en milisegundos (Crucial para el flujo de datos y RNF-03)
    val timestamp: Long = System.currentTimeMillis()
)
