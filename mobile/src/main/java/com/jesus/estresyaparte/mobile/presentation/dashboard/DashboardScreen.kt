package com.jesus.estresyaparte.mobile.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesus.estresyaparte.shared.BiometricData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    // Escuchamos de forma reactiva el estado del ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estrés y Aparte - Salud Laboral", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección 7: Banner Informativo de Desactualización (RNF-03)
            AnimatedVisibility(
                visible = uiState.showOutdatedWarning,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFA726)) // Color Naranja/Amarillo requerido
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Los datos mostrados no son los más recientes. Verifique la conexión Wi-Fi del reloj.",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // RF-07: Indicador de Estado de Sincronización
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Estado del Monitor:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (uiState.isLive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = if (uiState.isLive) "● EN VIVO" else "⏱️ DATOS HISTÓRICOS",
                            color = if (uiState.isLive) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                HorizontalDivider()

                // RF-06: Visualización de Métricas del Sensor
                val data = uiState.latestData
                if (data != null) {
                    MetricCard(title = "Frecuencia Cardíaca", value = "${data.heartRate} LPM", subValue = "Base de usuario vinculada")
                    MetricCard(title = "Estado de Actividad", value = if (data.IsMoving) "En Movimiento" else "En Reposo", subValue = "Detección de sedentarismo activa")
                    MetricCard(title = "Luz Ambiental", value = "${data.ambientLight} Lux", subValue = "Evaluación de fatiga visual")
                } else {
                    // Estado inicial o vacío sin datos recibidos aún
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Esperando sincronización con el Smartwatch...", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, subValue: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subValue, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
