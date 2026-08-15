package com.silvercat.sparkcards.data

import com.silvercat.sparkcards.data.local.CardStateDao
import com.silvercat.sparkcards.data.local.CardStateEntity
import com.silvercat.sparkcards.data.model.CardCategory
import com.silvercat.sparkcards.data.model.CardContent
import com.silvercat.sparkcards.data.model.CardUiModel
import com.silvercat.sparkcards.data.settings.SettingsDataStore
import com.silvercat.sparkcards.data.source.CardContentSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

/**
 * Selects cards from an in-memory shuffled queue that is only rebuilt once
 * exhausted, so the pool stays finite and "completable" rather than an
 * infinite shuffle - the whole point being to avoid recreating a doomscroll
 * feed. A card only counts as read once its detail side has actually been
 * viewed (see [markCardRead] call sites in the UI layer), not merely swiped
 * past, so skimming headlines can't fast-forward through a round.
 */
class DefaultCardRepository(
    private val contentSource: CardContentSource,
    private val stateDao: CardStateDao,
    private val settings: SettingsDataStore,
    private val random: Random = Random.Default,
) : CardRepository {

    private val queueMutex = Mutex()
    private var queue = ArrayDeque<String>()

    override fun observeEnabledCategories(): Flow<Set<CardCategory>> =
        settings.enabledCategories

    override suspend fun setCategoryEnabled(category: CardCategory, enabled: Boolean) {
        settings.setCategoryEnabled(category, enabled)
    }

    override suspend fun getNextCard(): CardRepository.NextCard = queueMutex.withLock {
        val allCards = contentSource.getAll()
        val enabledCategories = settings.enabledCategories.first()
        if (enabledCategories.isEmpty()) {
            return@withLock CardRepository.NextCard.NoCategoriesEnabled
        }

        val stateByCardId = stateDao.observeAll().first().associateBy { it.cardId }

        if (queue.isEmpty()) {
            val candidates = allCards.filter { card ->
                card.category in enabledCategories && stateByCardId[card.id]?.isRead != true
            }
            if (candidates.isEmpty()) {
                return@withLock CardRepository.NextCard.CyclePassComplete
            }
            queue = ArrayDeque(candidates.map { it.id }.shuffled(random))
        }

        val nextId = queue.removeFirst()
        val content = allCards.first { it.id == nextId }
        val state = stateByCardId[nextId]
        CardRepository.NextCard.Card(content.toUiModel(state))
    }

    override suspend fun markCardRead(cardId: String) {
        val current = stateDao.getState(cardId) ?: CardStateEntity(cardId = cardId)
        stateDao.upsert(current.copy(isRead = true))
    }

    override suspend fun toggleFavorite(cardId: String) {
        val current = stateDao.getState(cardId) ?: CardStateEntity(cardId = cardId)
        val nowFavorite = !current.isFavorite
        stateDao.upsert(
            current.copy(
                isFavorite = nowFavorite,
                favoritedAt = if (nowFavorite) System.currentTimeMillis() else null,
            )
        )
    }

    override fun observeFavorites(): Flow<List<CardUiModel>> =
        stateDao.observeFavorites().map { favoriteStates ->
            val allCards = contentSource.getAll()
            val contentById = allCards.associateBy { it.id }
            favoriteStates.mapNotNull { state ->
                contentById[state.cardId]?.toUiModel(state)
            }
        }

    override suspend fun resetCycle(): Unit = queueMutex.withLock {
        stateDao.resetAllRead()
        queue = ArrayDeque()
    }

    private fun CardContent.toUiModel(state: CardStateEntity?) = CardUiModel(
        content = this,
        isFavorite = state?.isFavorite == true,
        isRead = state?.isRead == true,
    )
}
