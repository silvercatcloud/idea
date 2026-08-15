package com.silvercat.sparkcards.data

import com.silvercat.sparkcards.data.model.CardCategory
import com.silvercat.sparkcards.data.model.CardUiModel
import kotlinx.coroutines.flow.Flow

interface CardRepository {

    sealed interface NextCard {
        data class Card(val card: CardUiModel) : NextCard
        data object CyclePassComplete : NextCard
        data object NoCategoriesEnabled : NextCard
    }

    fun observeEnabledCategories(): Flow<Set<CardCategory>>
    suspend fun setCategoryEnabled(category: CardCategory, enabled: Boolean)

    suspend fun getNextCard(): NextCard
    suspend fun markCardRead(cardId: String)
    suspend fun toggleFavorite(cardId: String)
    fun observeFavorites(): Flow<List<CardUiModel>>
    suspend fun resetCycle()
}
