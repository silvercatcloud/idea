package com.silvercat.sparkcards.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.silvercat.sparkcards.R
import com.silvercat.sparkcards.data.model.CardUiModel
import com.silvercat.sparkcards.ui.components.CategoryChip

@Composable
fun FlipCard(
    card: CardUiModel,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "cardFlip",
    )
    val density = LocalDensity.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onFlip)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 16f * density.density
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            if (rotation <= 90f) {
                CardFrontFace(card = card, onToggleFavorite = onToggleFavorite)
            } else {
                Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                    CardBackFace(card = card, onToggleFavorite = onToggleFavorite)
                }
            }
        }
    }
}

@Composable
private fun CardFrontFace(card: CardUiModel, onToggleFavorite: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CategoryChip(category = card.content.category)
            FavoriteButton(isFavorite = card.isFavorite, onClick = onToggleFavorite)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = card.content.title, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = card.content.hook,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
            )
            Text(
                text = stringResource(R.string.home_tap_to_flip),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun CardBackFace(card: CardUiModel, onToggleFavorite: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = card.content.title, style = MaterialTheme.typography.titleLarge)
            FavoriteButton(isFavorite = card.isFavorite, onClick = onToggleFavorite)
        }
        Text(
            text = card.content.detail,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        card.content.source?.let { source ->
            Text(
                text = "— $source",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
            contentDescription = stringResource(
                if (isFavorite) R.string.action_unfavorite else R.string.action_favorite
            ),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
