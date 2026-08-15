package com.silvercat.sparkcards.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvercat.sparkcards.data.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: CardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadNextCard()
    }

    private fun loadNextCard() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            _uiState.value = when (val next = repository.getNextCard()) {
                is CardRepository.NextCard.Card -> HomeUiState.ShowingCard(next.card, isFlipped = false)
                CardRepository.NextCard.CyclePassComplete -> HomeUiState.CyclePassComplete
                CardRepository.NextCard.NoCategoriesEnabled -> HomeUiState.NoCategoriesEnabled
            }
        }
    }

    fun onFlip() {
        val state = _uiState.value
        if (state !is HomeUiState.ShowingCard || state.isFlipped) return
        _uiState.value = state.copy(isFlipped = true)
        viewModelScope.launch {
            repository.markCardRead(state.card.content.id)
        }
    }

    fun onSwipeNext() {
        loadNextCard()
    }

    fun onToggleFavorite() {
        val state = _uiState.value
        if (state !is HomeUiState.ShowingCard) return
        viewModelScope.launch {
            repository.toggleFavorite(state.card.content.id)
            _uiState.value = state.copy(card = state.card.copy(isFavorite = !state.card.isFavorite))
        }
    }

    fun onRestartCycle() {
        viewModelScope.launch {
            repository.resetCycle()
            loadNextCard()
        }
    }
}
