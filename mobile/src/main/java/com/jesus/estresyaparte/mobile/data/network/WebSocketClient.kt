package com.jesus.estresyaparte.mobile.data.network

import com.jesus.estresyaparte.shared.BiometricData
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val onDataReceived: (BiometricData) -> Unit,
    private val onError: (String) -> Unit
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // Mantener la conexión abierta indefinidamente
        .build()

    private var webSocket: WebSocket? = null

    fun connect(url: String) {
        val request = Request.Builder().url(url).build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Conexión establecida con éxito con el backend en la nube
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // RF-06: Aquí llega el JSON string en tiempo real desde la Nube
                try {
                    // Nota: En un entorno de producción, deserializarías el JSON a la clase BiometricData
                    // Por ahora mapeamos una simulación reactiva al recibir la señal del servidor
                    val simulatedData = BiometricData(
                        userId = "OPERARIO_LOGISTICA_01",
                        heartRate = (70..110).random(), // Simula picos o descensos de pulso
                        IsMoving = (0..1).random() == 1,
                        ambientLight = 300f
                    )
                    
                    // Notificamos al callback (irá directo al ViewModel)
                    onDataReceived(simulatedData)
                } catch (e: Exception) {
                    onError("Error al procesar datos del servidor")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onError("Conexión perdida con el servidor: ${t.localizedMessage}")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Cierre controlado por la App móvil")
    }
}
