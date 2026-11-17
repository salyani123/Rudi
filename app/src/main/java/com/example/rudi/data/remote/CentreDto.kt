package com.example.rudi.data.remote

data class CentreDto(
    val id: Long = 0,
    val name: String? = null,
    val city: String? = null,
    val address: String? = null,
    val hours: String? = null,
    val requirements: String? = null,
    val acceptedTypes: List<String>? = null,
    val mapQuery: String? = null,
    val sourceUrl: String? = null,
    val verificationNote: String? = null
)
