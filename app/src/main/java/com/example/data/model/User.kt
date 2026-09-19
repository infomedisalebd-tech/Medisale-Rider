package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String, // e.g. info.medisalebd@gmail.com or rider email/username
    val password: String, // e.g. Jubayer6
    val name: String,
    val role: String = ROLE_ADMIN, // "ADMIN" or "RIDER"
    val riderId: Long? = null, // Linked Rider ID if role == "RIDER"
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_ADMIN = "ADMIN"
        const val ROLE_RIDER = "RIDER"
    }
}
