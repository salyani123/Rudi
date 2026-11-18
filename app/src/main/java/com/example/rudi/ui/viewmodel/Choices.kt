package com.example.rudi.ui.viewmodel

object Choices {
    val deviceTypes = listOf("Laptop", "Phone", "Charger", "Small appliance")
    val conditions = listOf("Working", "Not working", "Damaged")
    val cities = listOf("Nairobi", "Mombasa", "Kisumu", "Nakuru", "Eldoret")
    const val ALL_DEVICES = "All devices"
    val deviceFilter = listOf(ALL_DEVICES) + deviceTypes
}
