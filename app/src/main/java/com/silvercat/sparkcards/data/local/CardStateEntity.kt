package com.silvercat.sparkcards.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_state")
data class CardStateEntity(
    @PrimaryKey val cardId: String,
    val isRead: Boolean = false,
    val isFavorite: Boolean = false,
    val favoritedAt: Long? = null,
)
