package com.silvercat.sparkcards.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.silvercat.sparkcards.R
import com.silvercat.sparkcards.data.model.CardCategory

fun categoryLabelRes(category: CardCategory): Int = when (category) {
    CardCategory.TERMINOLOGY -> R.string.category_terminology
    CardCategory.MOVIE -> R.string.category_movie
    CardCategory.BOOK -> R.string.category_book
    CardCategory.PERSON -> R.string.category_person
    CardCategory.ECONOMICS -> R.string.category_economics
    CardCategory.POLITICS -> R.string.category_politics
    CardCategory.HISTORY -> R.string.category_history
}

@Composable
fun CategoryChip(category: CardCategory, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(categoryLabelRes(category)),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}
