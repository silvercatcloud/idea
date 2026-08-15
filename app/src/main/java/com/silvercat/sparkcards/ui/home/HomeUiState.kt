package com.silvercat.sparkcards.ui.home

import com.silvercat.sparkcards.data.model.CardUiModel

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class ShowingCard(val card: CardUiModel, val isFlipped: Boolean) : HomeUiState
    data object CyclePassComplete : HomeUiState
    data object NoCategoriesEnabled : HomeUiState
}
