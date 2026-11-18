package com.example.rudi.ui

import com.example.rudi.data.model.PickupRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val displayFormat = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

fun displayDate(iso: String): String = try {
    LocalDate.parse(iso).format(displayFormat)
} catch (_: DateTimeParseException) {
    iso
}

fun itemTitle(deviceType: String, brand: String, model: String) =
    listOf(brand, model).filter { it.isNotBlank() }.joinToString(" ").ifBlank { deviceType }

fun PickupRequest.title() = itemTitle(deviceType, brand, model)

fun PickupRequest.slotText() = displayDate(pickupDate) + " · " + timeSlot.replace('-', '–')
