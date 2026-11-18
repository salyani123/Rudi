package com.example.rudi.ui.viewmodel

import com.example.rudi.data.model.PickupRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

val pickupSlots = listOf("09:00-11:00", "14:00-16:00")
val nairobiZone: ZoneId = ZoneId.of("Africa/Nairobi")

fun pickupWindow(date: String, slot: String): Pair<LocalDateTime, LocalDateTime>? {
    if (slot !in pickupSlots) return null
    return try {
        val day = LocalDate.parse(date)
        day.atTime(LocalTime.parse(slot.substringBefore('-'))) to
            day.atTime(LocalTime.parse(slot.substringAfter('-')))
    } catch (_: DateTimeParseException) {
        null
    }
}

data class PickupDraft(
    val deviceType: String = "Laptop",
    val brand: String = "",
    val model: String = "",
    val condition: String = "Not working",
    val quantity: String = "1",
    val notes: String = "",
    val address: String = "",
    val date: String = "",
    val slot: String = pickupSlots.first(),
) {
    fun itemErrors(): Map<String, String> = buildMap {
        val count = quantity.trim().toIntOrNull()
        if (count == null || count !in 1..99) put("quantity", "Enter a quantity from 1 to 99")
        if (deviceType !in Choices.deviceTypes) put("deviceType", "Choose a device type")
        if (condition !in Choices.conditions) put("condition", "Choose a condition")
    }

    fun errors(now: LocalDateTime = LocalDateTime.now(nairobiZone)): Map<String, String> = buildMap {
        putAll(itemErrors())
        if (address.isBlank()) put("address", "Enter a pickup address")
        val window = pickupWindow(date, slot)
        when {
            window == null -> put("date", "Choose a valid date and slot")
            !window.first.isAfter(now) -> put("date", "Choose a future pickup slot")
        }
    }

    fun toRequest(
        id: Long,
        createdAt: Long?,
        now: LocalDateTime = LocalDateTime.now(nairobiZone),
    ): PickupRequest {
        require(errors(now).isEmpty())
        return PickupRequest(
            id, deviceType, brand.trim(), model.trim(), condition, quantity.trim().toInt(),
            notes.trim(), address.trim(), date, slot, createdAt ?: System.currentTimeMillis()
        )
    }
}
