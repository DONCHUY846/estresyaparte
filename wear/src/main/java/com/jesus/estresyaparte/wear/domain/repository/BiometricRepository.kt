package com.jesus.estresyaparte.wear.domain.repository

import com.jesus.estresyaparte.shared.BiometricData

interface BiometricRepository {
    /**
     * Procesa la nueva captura de los sensores decidiendo si se envía 
     * directamente a la nube o si se almacena en el buffer local.
     */
    suspend fun processSensorCapture(data: BiometricData)

    /**
     * Intenta sincronizar los datos acumulados en el buffer local con la nube (Back-sync).
     */
    suspend fun syncLocalBufferWithCloud(): Boolean
}
