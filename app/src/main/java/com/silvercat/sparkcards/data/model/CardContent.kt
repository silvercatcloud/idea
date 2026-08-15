package com.silvercat.sparkcards.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CardContent(
    val id: String,
    val category: CardCategory,
    val title: String,
    val hook: String,
    val detail: String,
    val source: String? = null,
)
