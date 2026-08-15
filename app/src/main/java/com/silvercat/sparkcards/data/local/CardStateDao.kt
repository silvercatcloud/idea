package com.silvercat.sparkcards.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CardStateDao {

    @Query("SELECT * FROM card_state")
    fun observeAll(): Flow<List<CardStateEntity>>

    @Query("SELECT * FROM card_state WHERE isFavorite = 1 ORDER BY favoritedAt DESC")
    fun observeFavorites(): Flow<List<CardStateEntity>>

    @Query("SELECT * FROM card_state WHERE cardId = :id")
    suspend fun getState(id: String): CardStateEntity?

    @Upsert
    suspend fun upsert(state: CardStateEntity)

    @Query("UPDATE card_state SET isRead = 0")
    suspend fun resetAllRead()
}
