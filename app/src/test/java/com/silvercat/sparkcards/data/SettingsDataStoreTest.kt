package com.silvercat.sparkcards.data

import com.silvercat.sparkcards.data.model.CardCategory
import com.silvercat.sparkcards.data.settings.SettingsDataStore
import com.silvercat.sparkcards.fakes.FakePreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDataStoreTest {

    @Test
    fun `defaults to all categories enabled`() = runTest {
        val settings = SettingsDataStore(FakePreferencesDataStore())

        val enabled = settings.enabledCategories.first()

        assertEquals(CardCategory.entries.toSet(), enabled)
    }

    @Test
    fun `disabling a category removes it from the enabled set`() = runTest {
        val settings = SettingsDataStore(FakePreferencesDataStore())

        settings.setCategoryEnabled(CardCategory.POETRY, enabled = false)

        val enabled = settings.enabledCategories.first()
        assertTrue(CardCategory.POETRY !in enabled)
        assertEquals(CardCategory.entries.size - 1, enabled.size)
    }

    @Test
    fun `re-enabling a category adds it back`() = runTest {
        val settings = SettingsDataStore(FakePreferencesDataStore())

        settings.setCategoryEnabled(CardCategory.POETRY, enabled = false)
        settings.setCategoryEnabled(CardCategory.POETRY, enabled = true)

        val enabled = settings.enabledCategories.first()
        assertEquals(CardCategory.entries.toSet(), enabled)
    }

    @Test
    fun `cannot disable the last remaining category`() = runTest {
        val settings = SettingsDataStore(FakePreferencesDataStore())

        for (category in CardCategory.entries) {
            if (category != CardCategory.BOOK) {
                settings.setCategoryEnabled(category, enabled = false)
            }
        }
        // BOOK is now the only enabled category; this attempt must be a no-op.
        settings.setCategoryEnabled(CardCategory.BOOK, enabled = false)

        val enabled = settings.enabledCategories.first()
        assertEquals(setOf(CardCategory.BOOK), enabled)
    }
}
