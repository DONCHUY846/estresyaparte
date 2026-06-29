package com.jesus.estresyaparte.wear.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BiometricDao {

    // Inserta un dato en el buffer. Si hay conflicto de ID, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: BiometricEntity)

    // Obtiene todos los datos almacenados offline ordenados del más antiguo al más reciente
    // Esto cumple con el RNF-01 para priorizar el envío ordenado (Back-sync)
    @Query("SELECT * FROM biometric_buffer ORDER BY timestamp ASC")
    suspend fun getAllBufferedData(): List<BiometricEntity>

    // Borra los datos que ya fueron sincronizados con éxito en la nube
    @Query("DELETE FROM biometric_buffer WHERE id IN (:ids)")
    suspend fun deleteSyncedData(ids: List<String>)

    // Opcional para RNF-04: Cuenta cuántos registros hay para validar el límite de 4 horas
    @Query("SELECT COUNT(*) FROM biometric_buffer")
    suspend fun getRecordCount(): Int
}
