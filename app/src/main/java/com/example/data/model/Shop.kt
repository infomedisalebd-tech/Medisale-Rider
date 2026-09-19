package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shops")
data class Shop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ownerOrManager: String = "",
    val phone: String,
    val address: String,
    val area: String,
    val tradeLicenseOrDrugLic: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
