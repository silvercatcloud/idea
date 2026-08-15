package com.silvercat.sparkcards.fakes

import com.silvercat.sparkcards.data.local.CardStateDao
import com.silvercat.sparkcards.data.local.CardStateEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class FakeCardStateDao : CardStateDao {

    private val state = MutableStateFlow<Map<String, CardStateEntity>>(emptyMap())

    val current: StateFlow<Map<String, CardStateEntity>> = state

    override fun observeAll() = state.map { it.values.toList() }

    override fun observeFavorites() = state.map { map ->
        map.values.filter { it.isFavorite }.sortedByDescending { it.favoritedAt }
    }

    override suspend fun getState(id: String): CardStateEntity? = state.value[id]

    override suspend fun upsert(state: CardStateEntity) {
        this.state.value = this.state.value + (state.cardId to state)
    }

    override suspend fun resetAllRead() {
        state.value = state.value.mapValues { (_, entity) -> entity.copy(isRead = false) }
    }
}
