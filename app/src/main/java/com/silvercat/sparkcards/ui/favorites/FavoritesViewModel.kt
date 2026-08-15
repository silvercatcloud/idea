package com.silvercat.sparkcards.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvercat.sparkcards.data.CardRepository
import com.silvercat.sparkcards.data.model.CardUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: CardRepository) : ViewModel() {

    val favorites: StateFlow<List<CardUiModel>> = repository.observeFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onToggleFavorite(cardId: String) {
        viewModelScope.launch { repository.toggleFavorite(cardId) }
    }
}
