package com.example.rudi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pickup_requests")
data class PickupRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceType: String,
    val brand: String,
    val model: String,
    val condition: String,
    val quantity: Int,
    val notes: String,
    val address: String,
    val pickupDate: String,
    val timeSlot: String,
    val createdAt: Long = System.currentTimeMillis()
)
