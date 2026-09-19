package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "handover_logs")
data class HandoverLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val riderId: Long,
    val riderName: String,
    val riderPhone: String,
    val requestedCash: Double,
    val receivedCash: Double,
    val remainingCashDue: Double,
    val returnItemsCount: Int = 0,
    val returnItemsSummary: String = "",
    val cancelledOrdersCount: Int = 0,
    val cancelledOrdersSummary: String = "",
    val riderNotes: String = "",
    val adminNotes: String = "",
    val status: String = "RECEIVED", // "RECEIVED_FULL", "RECEIVED_PARTIAL"
    val timestamp: Long = System.currentTimeMillis()
)
