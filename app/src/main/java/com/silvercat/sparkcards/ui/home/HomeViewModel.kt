package com.silvercat.sparkcards.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvercat.sparkcards.data.CardRepository
import com.silvercat.sparkcards.data.model.CardUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Browser-history-style navigation: [visited] holds every card shown so far
 * this session, [currentIndex] points at the one on screen. Swiping forward
 * past the end of that history pulls a genuinely new card from the
 * repository; swiping back just moves the index, so glancing back never
 * consumes a new card from the unread pool.
 */
class HomeViewModel(private val repository: CardRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val visited = mutableListOf<CardUiModel>()
    private var currentIndex = -1

    init {
        advance()
    }

    private fun advance() {
        if (currentIndex < visited.lastIndex) {
            currentIndex++
            _uiState.value = HomeUiState.ShowingCard(visited[currentIndex], isFlipped = false)
            return
        }
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            when (val next = repository.getNextCard()) {
                is CardRepository.NextCard.Card -> {
                    visited.add(next.card)
                    currentIndex = visited.lastIndex
                    _uiState.value = HomeUiState.ShowingCard(next.card, isFlipped = false)
                }
                CardRepository.NextCard.CyclePassComplete -> _uiState.value = HomeUiState.CyclePassComplete
                CardRepository.NextCard.NoCategoriesEnabled -> _uiState.value = HomeUiState.NoCategoriesEnabled
            }
        }
    }

    fun onSwipeNext() = advance()

    fun onSwipePrevious() {
        if (currentIndex <= 0) return
        currentIndex--
        _uiState.value = HomeUiState.ShowingCard(visited[currentIndex], isFlipped = false)
    }

    fun onFlip() {
        val state = _uiState.value
        if (state !is HomeUiState.ShowingCard || state.isFlipped) return
        _uiState.value = state.copy(isFlipped = true)
        viewModelScope.launch {
            repository.markCardRead(state.card.content.id)
        }
    }

    fun onToggleFavorite() {
        val state = _uiState.value
        if (state !is HomeUiState.ShowingCard) return
        viewModelScope.launch {
            repository.toggleFavorite(state.card.content.id)
            val updated = state.card.copy(isFavorite = !state.card.isFavorite)
            if (currentIndex in visited.indices) {
                visited[currentIndex] = updated
            }
            _uiState.value = state.copy(card = updated)
        }
    }

    fun onRestartCycle() {
        viewModelScope.launch {
            repository.resetCycle()
            visited.clear()
            currentIndex = -1
            advance()
        }
    }
}
