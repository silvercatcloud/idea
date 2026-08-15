package com.silvercat.sparkcards.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.silvercat.sparkcards.data.CardRepository
import com.silvercat.sparkcards.data.DefaultCardRepository
import com.silvercat.sparkcards.data.local.AppDatabase
import com.silvercat.sparkcards.data.settings.SettingsDataStore
import com.silvercat.sparkcards.data.source.AssetCardContentSource
import com.silvercat.sparkcards.data.source.CardContentSource

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/**
 * Hand-rolled dependency container for a single-module app this small.
 * Owned once by [com.silvercat.sparkcards.SparkApplication]; screens reach
 * it via a small ViewModel factory rather than pulling in Hilt.
 */
class AppContainer(context: Context) {

    val cardContentSource: CardContentSource = AssetCardContentSource(context)

    private val database = AppDatabase.getInstance(context)
    private val settingsDataStore = SettingsDataStore(context.settingsDataStore)

    val cardRepository: CardRepository = DefaultCardRepository(
        contentSource = cardContentSource,
        stateDao = database.cardStateDao(),
        settings = settingsDataStore,
    )
}
