package com.jesus.estresyaparte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jesus.estresyaparte.mobile.presentation.dashboard.DashboardScreen
import com.jesus.estresyaparte.mobile.presentation.dashboard.DashboardViewModel
import com.jesus.estresyaparte.ui.theme.EstresyaparteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EstresyaparteTheme {
                val viewModel: DashboardViewModel = viewModel()
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
