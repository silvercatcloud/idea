package com.silvercat.sparkcards.data.source

import android.content.Context
import com.silvercat.sparkcards.data.model.CardContent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Loads assets/cards.json from the app bundle exactly once, on first [preload]. */
class AssetCardContentSource(private val context: Context) : CardContentSource {

    private val cardsDeferred = CompletableDeferred<List<CardContent>>()

    override suspend fun preload() {
        val text = readAssetText()
        cardsDeferred.complete(CardContentSource.parse(text))
    }

    override suspend fun getAll(): List<CardContent> = cardsDeferred.await()

    private suspend fun readAssetText(): String = withContext(Dispatchers.IO) {
        context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
    }

    private companion object {
        const val ASSET_NAME = "cards.json"
    }
}
