package com.example.cardadvisor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,           // e.g. "Chase Sapphire Preferred"
    val lastFour: String,       // e.g. "4242"
    val network: String,        // e.g. "Visa", "Mastercard", "Amex"
    val color: Long = 0xFF1A73E8, // card color as ARGB long
    val centsPerPoint: Double = 1.0  // used to convert points → effective cashback
)
