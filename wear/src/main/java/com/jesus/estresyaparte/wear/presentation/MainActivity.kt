package com.jesus.estresyaparte.wear.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.jesus.estresyaparte.wear.data.service.SensorForegroundService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearApp(
                onStartTracking = { startSensorService() },
                onStopTracking = { stopSensorService() }
            )
        }
    }

    private fun startSensorService() {
        val intent = Intent(this, SensorForegroundService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopSensorService() {
        val intent = Intent(this, SensorForegroundService::class.java)
        stopService(intent)
    }
}

@Composable
fun WearApp(onStartTracking: () -> Unit, onStopTracking: () -> Unit) {
    var isTracking by remember { mutableStateOf(false) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Estrés y Aparte",
                style = MaterialTheme.typography.caption1,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = if (isTracking) "Monitoreando..." else "Monitoreo Pausado",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                color = if (isTracking) Color.Green else Color.Yellow,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = {
                    isTracking = !isTracking
                    if (isTracking) onStartTracking() else onStopTracking()
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isTracking) Color.Red else Color.Blue
                ),
                modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
            ) {
                Text(
                    text = if (isTracking) "Detener" else "Iniciar",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}
