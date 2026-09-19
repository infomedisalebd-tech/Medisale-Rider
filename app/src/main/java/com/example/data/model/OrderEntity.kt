package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DeliveryStatus(val label: String) {
    DISPATCHED("Dispatched (চলমান)"),
    DELIVERED("Delivered (সম্পন্ন)"),
    PARTIALLY_RETURNED("Partial Return (আংশিক ফেরত)"),
    RETURNED("Full Return (সম্পূর্ণ ফেরত)"),
    CANCELLED("Cancelled (বাতিল)")
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val shopId: Long = 0,
    val shopName: String,
    val shopPhone: String,
    val shopAddress: String,
    val riderId: Long = 0,
    val riderName: String,
    val riderPhone: String,
    val orderDate: Long = System.currentTimeMillis(),
    val assignedTime: String = "",
    val deliveryTime: String? = null, // koytay delivery dilo
    val status: String = DeliveryStatus.DISPATCHED.name,
    val grossAmount: Double = 0.0,     // mot item gular gross price
    val returnAmount: Double = 0.0,    // return item gular amount
    val discount: Double = 0.0,        // special discount
    val netAmount: Double = 0.0,       // grossAmount - returnAmount - discount
    val paidAmount: Double = 0.0,      // koto taka collection hoiche
    val dueAmount: Double = 0.0,       // koto taka baki ache (netAmount - paidAmount)
    val paymentMethod: String = "Cash", // Cash, bKash/Nagad, Credit/Due
    val notes: String = "",
    val cancelReason: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Handover & Admin Settlement tracking
    val adminReceivedCash: Double = 0.0,       // How much of paidAmount has been confirmed and received by Admin
    val adminReceivedReturn: Boolean = false,   // Whether Admin received the returned medicine items
    val adminReceivedCancel: Boolean = false,   // Whether Admin received the cancelled parcel items
    val handoverRequested: Boolean = false,     // Rider submitted handover request for this order
    val handoverRequestedCash: Double = 0.0,   // Cash rider declared in handover request
    val handoverNotes: String = "",            // Note left by rider during handover
    val handoverRequestedAt: Long? = null,     // Timestamp when rider requested
    val adminSettledAt: Long? = null,          // Timestamp when admin confirmed settlement
    val adminSettlementNote: String = ""       // Note left by admin upon receiving
)
