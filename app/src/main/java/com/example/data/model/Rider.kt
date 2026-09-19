package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riders")
data class Rider(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String,
    val vehicleType: String = "Motorcycle", // Motorcycle, Bicycle, Van
    val zone: String = "Dhaka Central",
    val loginEmail: String = "",
    val loginPassword: String = "",
    val isActive: Boolean = true,
    val joinedDate: Long = System.currentTimeMillis()
)
