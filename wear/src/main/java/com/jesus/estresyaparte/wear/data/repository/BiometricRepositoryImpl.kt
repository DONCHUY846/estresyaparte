package com.jesus.estresyaparte.wear.data.repository

import com.jesus.estresyaparte.shared.BiometricData
import com.jesus.estresyaparte.wear.data.local.BiometricDao
import com.jesus.estresyaparte.wear.data.local.BiometricEntity
import com.jesus.estresyaparte.wear.data.network.NetworkMonitor
import com.jesus.estresyaparte.wear.domain.repository.BiometricRepository

class BiometricRepositoryImpl(
    private val biometricDao: BiometricDao,
    private val networkMonitor: NetworkMonitor // Monitor inyectado
) : BiometricRepository {

    override suspend fun processSensorCapture(data: BiometricData) {
        // RF-03: Ahora validamos la red real de forma dinámica
        val isWifiConnected = networkMonitor.isWifiConnected.value 

        if (isWifiConnected) {
            // Caso Online: Envío directo a la nube vía API/WebSocket (RF-03)
            sendToCloud(data)
        } else {
            // Caso Offline: Guardar en el buffer local de Room (RF-03, RNF-04)
            val entity = BiometricEntity(
                id = data.id,
                userId = data.userId,
                heartRate = data.heartRate,
                isMoving = data.IsMoving,
                ambientLight = data.ambientLight,
                timestamp = data.timestamp
            )
            biometricDao.insertData(entity)
        }
    }

    override suspend fun syncLocalBufferWithCloud(): Boolean {
        // RNF-01: Extraer el buffer acumulado cronológicamente
        val bufferedRecords = biometricDao.getAllBufferedData()
        if (bufferedRecords.isEmpty()) return true

        try {
            // Enviamos los bloques de datos offline acumulados
            bufferedRecords.forEach { entity ->
                val dataToSync = BiometricData(
                    id = entity.id,
                    userId = entity.userId,
                    heartRate = entity.heartRate,
                    IsMoving = entity.isMoving,
                    ambientLight = entity.ambientLight,
                    timestamp = entity.timestamp
                )
                sendToCloud(dataToSync)
            }
            
            // Si la nube los recibió bien, limpiamos el buffer local
            val syncedIds = bufferedRecords.map { it.id }
            biometricDao.deleteSyncedData(syncedIds)
            return true
        } catch (e: Exception) {
            // Resiliencia: si falla la red a la mitad, permanecen en Room
            return false
        }
    }

    private suspend fun sendToCloud(data: BiometricData) {
        // Aquí se conectará el WebSocket definitivo
    }
}
