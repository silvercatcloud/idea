package com.silvercat.sparkcards.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silvercat.sparkcards.data.CardRepository
import com.silvercat.sparkcards.data.model.CardCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: CardRepository) : ViewModel() {

    val enabledCategories: StateFlow<Set<CardCategory>> = repository.observeEnabledCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CardCategory.entries.toSet())

    fun onCategoryToggled(category: CardCategory, enabled: Boolean) {
        viewModelScope.launch { repository.setCategoryEnabled(category, enabled) }
    }
}
