package com.jesus.estresyaparte.wear.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jesus.estresyaparte.shared.BiometricData
import com.jesus.estresyaparte.wear.data.local.AppDatabase
import com.jesus.estresyaparte.wear.data.repository.BiometricRepositoryImpl
import com.jesus.estresyaparte.wear.domain.repository.BiometricRepository
import kotlinx.coroutines.*
import kotlin.math.sqrt

class SensorForegroundService : Service(), SensorEventListener {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private lateinit var sensorManager: SensorManager
    private lateinit var repository: BiometricRepository

    // Variables temporales para guardar el último estado de los sensores
    private var currentHeartRate: Int = 75 // Base promedio
    private var isMoving: Boolean = false
    private var currentLightLevel: Float = 250f // Luz estándar de oficina
    
    private var samplingJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        
        // Inicializamos la base de datos y el repositorio manualmente
        val database = AppDatabase.getDatabase(applicationContext)
        repository = BiometricRepositoryImpl(database.biometricDao())

        registerSensors()
        startForegroundService()
        startSamplingLoop()
    }

    private fun registerSensors() {
        // 1. Ritmo Cardíaco
        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        // 2. Acelerómetro
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        // 3. Luz Ambiental
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // RF-03: Bucle de muestreo que envía datos en intervalos de 5 a 10 segundos
    private fun startSamplingLoop() {
        samplingJob = serviceScope.launch {
            while (isActive) {
                delay(7000) // 7 segundos (está en el intervalo de 5-10s solicitado)

                val capture = BiometricData(
                    userId = "OPERARIO_LOGISTICA_01", // ID dinámico en el futuro (RF-01)
                    heartRate = currentHeartRate,
                    IsMoving = isMoving,
                    ambientLight = currentLightLevel
                )
                
                // Manda el dato recopilado al repositorio (él sabrá si hay Wi-Fi o va al buffer)
                repository.processSensorCapture(capture)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_HEART_RATE -> {
                if (event.values.isNotEmpty()) {
                    currentHeartRate = event.values[0].toInt()
                }
            }
            Sensor.TYPE_LIGHT -> {
                if (event.values.isNotEmpty()) {
                    currentLightLevel = event.values[0]
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                // Calcular la magnitud del vector de movimiento (Fórmula matemática estándar)
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val gForce = sqrt(x*x + y*y + z*z)
                
                // Si la fuerza difiere significativamente de la gravedad terrestre (~9.81 m/s²), se está moviendo
                isMoving = gForce > 11.5 || gForce < 8.0
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startForegroundService() {
        val channelId = "sensor_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Monitoreo Salud Laboral", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Estrés y Aparte")
            .setContentText("Monitoreando signos vitales en tiempo real...")
            .setSmallIcon(android.R.drawable.ic_menu_compass) // Reemplazar por un icono propio
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        samplingJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
