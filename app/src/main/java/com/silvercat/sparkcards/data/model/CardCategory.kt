package com.silvercat.sparkcards.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CardCategory {
    TERMINOLOGY,
    MOVIE,
    BOOK,
    POETRY,
    PERSON,
}
