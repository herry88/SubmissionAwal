package com.belajar.submissionawal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_event")
data class FavoriteEvent(
    @PrimaryKey
    val id: Int,
    val name: String,
    val mediaCover: String? = null
)
