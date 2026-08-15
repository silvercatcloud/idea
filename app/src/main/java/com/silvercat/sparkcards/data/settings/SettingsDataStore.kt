package com.silvercat.sparkcards.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.silvercat.sparkcards.data.model.CardCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Wraps a Preferences DataStore for the user's enabled-category filter.
 * [dataStore] is injected so it can be backed by a temp file in JVM tests
 * without any Android Context.
 */
class SettingsDataStore(private val dataStore: DataStore<Preferences>) {

    private val enabledCategoriesKey = stringSetPreferencesKey("enabled_categories")

    private val allCategories = CardCategory.entries.map { it.name }.toSet()

    val enabledCategories: Flow<Set<CardCategory>> = dataStore.data.map { prefs ->
        val stored = prefs[enabledCategoriesKey] ?: allCategories
        stored.mapNotNull { name ->
            runCatching { CardCategory.valueOf(name) }.getOrNull()
        }.toSet().ifEmpty { CardCategory.entries.toSet() }
    }

    suspend fun setCategoryEnabled(category: CardCategory, enabled: Boolean) {
        dataStore.edit { prefs ->
            val current = prefs[enabledCategoriesKey] ?: allCategories
            val updated = if (enabled) current + category.name else current - category.name
            // Guard: never allow the last enabled category to be turned off.
            if (updated.isNotEmpty()) {
                prefs[enabledCategoriesKey] = updated
            }
        }
    }
}
