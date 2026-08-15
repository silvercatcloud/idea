package com.silvercat.sparkcards.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.silvercat.sparkcards.R
import com.silvercat.sparkcards.data.CardRepository
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun HomeScreen(repository: CardRepository) {
    val viewModel: HomeViewModel = viewModel(
        factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        },
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        when (val state = uiState) {
            HomeUiState.Loading -> LoadingContent()
            is HomeUiState.ShowingCard -> SwipeableCard(
                state = state,
                onFlip = viewModel::onFlip,
                onToggleFavorite = viewModel::onToggleFavorite,
                onSwipeNext = viewModel::onSwipeNext,
            )
            HomeUiState.CyclePassComplete -> CyclePassCompleteContent(onRestart = viewModel::onRestartCycle)
            HomeUiState.NoCategoriesEnabled -> NoCategoriesContent()
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.home_loading),
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun SwipeableCard(
    state: HomeUiState.ShowingCard,
    onFlip: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSwipeNext: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    var swipeThreshold by remember { mutableStateOf(600f) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .pointerInput(state.card.content.id) {
                    swipeThreshold = size.width / 3f
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (abs(offsetX.value) > swipeThreshold) {
                                    offsetX.animateTo(if (offsetX.value > 0) 1200f else -1200f)
                                    offsetX.snapTo(0f)
                                    onSwipeNext()
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                        },
                    )
                },
        ) {
            FlipCard(
                card = state.card,
                isFlipped = state.isFlipped,
                onFlip = onFlip,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = offsetX.value
                        rotationZ = (offsetX.value / 60f).coerceIn(-12f, 12f)
                    },
            )
        }

        Text(
            text = stringResource(R.string.home_swipe_hint),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )

        Button(
            onClick = {
                scope.launch {
                    offsetX.animateTo(-1200f)
                    offsetX.snapTo(0f)
                    onSwipeNext()
                }
            },
            modifier = Modifier.padding(top = 8.dp),
        ) {
            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun CyclePassCompleteContent(onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.home_cycle_complete_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.home_cycle_complete_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Button(onClick = onRestart) {
            Text(text = stringResource(R.string.home_cycle_restart))
        }
    }
}

@Composable
private fun NoCategoriesContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.home_no_categories_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.home_no_categories_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
