package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteRoute(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromStand: String,
    val fromStandMr: String,
    val toDestination: String,
    val toDestinationMr: String,
    val savedAt: Long = System.currentTimeMillis()
)
