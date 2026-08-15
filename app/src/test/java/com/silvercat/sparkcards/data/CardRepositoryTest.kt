package com.silvercat.sparkcards.data

import com.silvercat.sparkcards.data.model.CardCategory
import com.silvercat.sparkcards.data.model.CardContent
import com.silvercat.sparkcards.data.settings.SettingsDataStore
import com.silvercat.sparkcards.fakes.FakeCardContentSource
import com.silvercat.sparkcards.fakes.FakeCardStateDao
import com.silvercat.sparkcards.fakes.FakePreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class CardRepositoryTest {

    private val allCards = listOf(
        card("term_1", CardCategory.TERMINOLOGY),
        card("term_2", CardCategory.TERMINOLOGY),
        card("movie_1", CardCategory.MOVIE),
        card("movie_2", CardCategory.MOVIE),
    )

    private lateinit var stateDao: FakeCardStateDao
    private lateinit var settings: SettingsDataStore
    private lateinit var repository: DefaultCardRepository

    @Before
    fun setUp() {
        stateDao = FakeCardStateDao()
        settings = SettingsDataStore(FakePreferencesDataStore())
        repository = DefaultCardRepository(
            contentSource = FakeCardContentSource(allCards),
            stateDao = stateDao,
            settings = settings,
            random = Random(42),
        )
    }

    private fun card(id: String, category: CardCategory) = CardContent(
        id = id,
        category = category,
        title = id,
        hook = "hook-$id",
        detail = "detail-$id",
    )

    @Test
    fun `getNextCard only returns cards from enabled categories`() = runTest {
        settings.setCategoryEnabled(CardCategory.MOVIE, enabled = false)

        val seen = mutableSetOf<String>()
        repeat(2) {
            val next = repository.getNextCard()
            check(next is CardRepository.NextCard.Card)
            seen += next.card.content.id
            repository.markCardRead(next.card.content.id)
        }

        assertEquals(setOf("term_1", "term_2"), seen)
    }

    @Test
    fun `a card only leaves the unread pool once its detail is marked read`() = runTest {
        val first = repository.getNextCard()
        check(first is CardRepository.NextCard.Card)
        // Swiped past without flipping: NOT marked read, so it must resurface once the
        // rest of the current pass's queue (the other 3 cards) has been drained.

        val secondsSeen = mutableSetOf<String>()
        repeat(allCards.size) {
            val next = repository.getNextCard()
            check(next is CardRepository.NextCard.Card)
            secondsSeen += next.card.content.id
            repository.markCardRead(next.card.content.id)
        }

        assertTrue("unread card should resurface", first.card.content.id in secondsSeen)
    }

    @Test
    fun `CyclePassComplete fires once every enabled card has been read`() = runTest {
        settings.setCategoryEnabled(CardCategory.MOVIE, enabled = false)

        repeat(2) {
            val next = repository.getNextCard()
            check(next is CardRepository.NextCard.Card)
            repository.markCardRead(next.card.content.id)
        }

        assertEquals(CardRepository.NextCard.CyclePassComplete, repository.getNextCard())
    }

    @Test
    fun `resetCycle clears read state but preserves favorites`() = runTest {
        settings.setCategoryEnabled(CardCategory.MOVIE, enabled = false)
        repeat(2) {
            val next = repository.getNextCard()
            check(next is CardRepository.NextCard.Card)
            repository.markCardRead(next.card.content.id)
        }
        repository.toggleFavorite("term_1")
        assertEquals(CardRepository.NextCard.CyclePassComplete, repository.getNextCard())

        repository.resetCycle()

        val next = repository.getNextCard()
        check(next is CardRepository.NextCard.Card)
        val favorites = repository.observeFavorites().first()
        assertEquals(listOf("term_1"), favorites.map { it.content.id })
    }

    @Test
    fun `toggleFavorite flips state and is reflected in observeFavorites`() = runTest {
        assertTrue(repository.observeFavorites().first().isEmpty())

        repository.toggleFavorite("movie_1")
        assertEquals(listOf("movie_1"), repository.observeFavorites().first().map { it.content.id })

        repository.toggleFavorite("movie_1")
        assertTrue(repository.observeFavorites().first().isEmpty())
    }
}
