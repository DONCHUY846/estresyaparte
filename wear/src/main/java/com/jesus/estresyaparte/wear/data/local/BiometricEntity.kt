package com.jesus.estresyaparte.wear.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "biometric_buffer")
data class BiometricEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val heartRate: Int,
    val isMoving: Boolean,
    val ambientLight: Float,
    val timestamp: Long
)
