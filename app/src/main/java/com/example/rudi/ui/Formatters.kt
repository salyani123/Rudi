package com.example.rudi.ui

import com.example.rudi.data.model.PickupRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun displayDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun itemTitle(deviceType: String, brand: String, model: String): String {
    val parts = listOf(brand, model).filter { it.isNotBlank() }
    val details = if (parts.isNotEmpty()) " (${parts.joinToString(" ")})" else ""
    return "$deviceType$details"
}

fun PickupRequest.title(): String {
    return itemTitle(deviceType, brand, model)
}

fun PickupRequest.slotText(): String {
    return "$pickupDate • $timeSlot"
}
