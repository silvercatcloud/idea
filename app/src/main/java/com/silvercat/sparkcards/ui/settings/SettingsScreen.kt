package com.silvercat.sparkcards.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import com.silvercat.sparkcards.R
import com.silvercat.sparkcards.data.CardRepository
import com.silvercat.sparkcards.data.model.CardCategory
import com.silvercat.sparkcards.ui.components.categoryLabelRes

@Composable
fun SettingsScreen(repository: CardRepository) {
    val viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { SettingsViewModel(repository) }
        },
    )
    val enabledCategories by viewModel.enabledCategories.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            text = stringResource(R.string.settings_categories_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.settings_categories_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )
        CardCategory.entries.forEach { category ->
            val enabled = category in enabledCategories
            val isLastEnabled = enabled && enabledCategories.size == 1
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(categoryLabelRes(category)),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Switch(
                    checked = enabled,
                    enabled = !isLastEnabled,
                    onCheckedChange = { checked ->
                        viewModel.onCategoryToggled(category, checked)
                    },
                )
            }
        }
    }
}
