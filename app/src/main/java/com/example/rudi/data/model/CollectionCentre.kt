package com.example.rudi.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_centres")
data class CollectionCentre(
    @PrimaryKey val id: Long,
    val name: String,
    val city: String,
    val address: String,
    val hours: String,
    val requirements: String,
    val acceptedTypes: String,
    val mapQuery: String,
    val sourceUrl: String,
    val verificationNote: String
)
