package com.jesus.estresyaparte.wear.data.repository

import com.jesus.estresyaparte.shared.BiometricData
import com.jesus.estresyaparte.wear.data.local.BiometricDao
import com.jesus.estresyaparte.wear.data.local.BiometricEntity
import com.jesus.estresyaparte.wear.domain.repository.BiometricRepository

class BiometricRepositoryImpl(
    private val biometricDao: BiometricDao
    // Aquí inyectaremos el servicio de API/WebSocket en la Fase 3
) : BiometricRepository {

    override suspend fun processSensorCapture(data: BiometricData) {
        val isWifiConnected = checkNetworkConnectivity() // Lógica de validación de red (RF-03)

        if (isWifiConnected) {
            // Caso Online: Envío directo a la nube (RF-03)
            sendToCloud(data)
        } else {
            // Caso Offline: Mapeo y almacenamiento en el buffer local de Room (RF-03 / RNF-04)
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
        // RNF-01: Al recuperar la conexión, prioriza el envío del buffer acumulado
        val bufferedRecords = biometricDao.getAllBufferedData()
        if (bufferedRecords.isEmpty()) return true

        try {
            // Simulamos el envío en bloque a la nube
            bufferedRecords.forEach { entity ->
                // sendToCloud(...)
            }
            
            // Si todo se envió con éxito, limpiamos esos IDs del buffer local
            val syncedIds = bufferedRecords.map { it.id }
            biometricDao.deleteSyncedData(syncedIds)
            return true
        } catch (e: Exception) {
            // Si falla la red a mitad del proceso, los datos se quedan seguros en Room
            return false
        }
    }

    // Función auxiliar para simular la detección de Wi-Fi temporalmente
    private fun checkNetworkConnectivity(): Boolean {
        // En la Fase 3 usaremos el ConnectivityManager real de Android
        return false // Forzamos false para probar el buffer local primero
    }

    private suspend fun sendToCloud(data: BiometricData) {
        // Se conectará en la siguiente fase vía Ktor/WebSockets
    }
}
