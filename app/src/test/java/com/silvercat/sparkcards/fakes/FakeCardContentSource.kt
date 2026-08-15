package com.silvercat.sparkcards.fakes

import com.silvercat.sparkcards.data.model.CardContent
import com.silvercat.sparkcards.data.source.CardContentSource

class FakeCardContentSource(private val cards: List<CardContent>) : CardContentSource {
    override suspend fun preload() = Unit
    override suspend fun getAll(): List<CardContent> = cards
}
