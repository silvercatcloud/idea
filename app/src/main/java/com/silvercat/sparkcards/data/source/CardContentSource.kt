package com.silvercat.sparkcards.data.source

import com.silvercat.sparkcards.data.model.CardContent
import kotlinx.serialization.json.Json

/**
 * Provides the bundled static card library. Kept as an interface so
 * [DefaultCardRepository][com.silvercat.sparkcards.data.DefaultCardRepository]
 * can be unit-tested with a fake in-memory implementation, without needing
 * an Android Context or Robolectric.
 */
interface CardContentSource {
    suspend fun preload()
    suspend fun getAll(): List<CardContent>

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        /** Pure, unit-testable parsing of the cards.json contents. */
        fun parse(jsonText: String): List<CardContent> = json.decodeFromString(jsonText)
    }
}
