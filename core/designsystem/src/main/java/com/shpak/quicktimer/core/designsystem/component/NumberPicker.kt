package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shpak.quicktimer.core.designsystem.theme.TimerTextStyles
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    rowHeight: Dp = 52.dp,
    label: (Int) -> String = ::twoDigits
) {
    val valuesCount = range.last - range.first + 1
    val sideRowsCount = 2
    val visibleRows = sideRowsCount * 2 + 1
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = value - range.first
    )
    val rowHeightPx = with(LocalDensity.current) {
        rowHeight.toPx()
    }
    val centeredIndex by remember(state, rowHeightPx, valuesCount) {
        derivedStateOf { state.centeredIndex(rowHeightPx, valuesCount) }
    }
    var isAutoscrolling by remember { mutableStateOf(false) }
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    LaunchedEffect(state, range) {
        snapshotFlow {
            centeredIndex.takeUnless { isAutoscrolling }
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { index ->
                val selectedNumber = range.first + index
                if (selectedNumber != currentValue) {
                    currentOnValueChange(selectedNumber)
                }
            }
    }

    LaunchedEffect(value, range) {
        val targetIndex = (value - range.first).coerceIn(0..<valuesCount)
        if (targetIndex != centeredIndex && !state.isScrollInProgress) {
            isAutoscrolling = true
            try {
                state.animateScrollToItem(targetIndex)
            } finally {
                isAutoscrolling = false
            }
        }
    }

    val fadeFraction = 0.25f
    Box(
        modifier = modifier
            .height(rowHeight * visibleRows)
            .clip(NumberPickerDefaults.Shape)
            .background(colors.containerColor)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(rowHeight)
                .padding(horizontal = 3.dp)
                .clip(NumberPickerDefaults.SelectionShape)
                .background(colors.selectionColor)
        )

        LazyColumn(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(state, SnapPosition.Center),
            contentPadding = PaddingValues(vertical = rowHeight * sideRowsCount),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent,
                            fadeFraction to Color.Black,
                            1f - fadeFraction to Color.Black,
                            1f to Color.Transparent
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
        ) {
            items(
                count = valuesCount,
                key = { index -> index }
            ) { index ->
                val isSelected by remember(index) {
                    derivedStateOf { index == centeredIndex }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .alpha(alphaForDistance(abs(index - centeredIndex)))
                ) {
                    Text(
                        text = label(range.first + index),
                        color = if (isSelected) {
                            colors.selectedContentColor
                        } else {
                            colors.contentColor
                        },
                        style = if (isSelected) {
                            TimerTextStyles.wheelSelected
                        } else {
                            TimerTextStyles.wheelUnselected
                        },
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private fun LazyListState.centeredIndex(rowHeightPx: Float, count: Int): Int {
    val offsetRows = if (rowHeightPx > 0f) {
        (firstVisibleItemScrollOffset / rowHeightPx).roundToInt()
    } else {
        0
    }

    return (firstVisibleItemIndex + offsetRows).coerceIn(0..<count)
}

private fun twoDigits(value: Int): String = value.toString().padStart(2, '0')

private fun alphaForDistance(distanceIndices: Int): Float = when (distanceIndices) {
    0 -> 1f
    1 -> 2 / 3f
    2 -> 1 / 3f
    else -> 1 / 6f
}

@Immutable
data class NumberPickerColors(
    val containerColor: Color,
    val selectionColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color,
)

object NumberPickerDefaults {
    internal val Shape = RoundedCornerShape(26.dp)
    internal val SelectionShape = RoundedCornerShape(16.dp)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        selectionColor: Color = MaterialTheme.colorScheme.primaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    ): NumberPickerColors = NumberPickerColors(
        containerColor = containerColor,
        selectionColor = selectionColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor,
    )
}

@Preview
@Composable
private fun NumberPickerPreview() {
    MaterialTheme {
        NumberPicker(
            value = 5,
            onValueChange = {},
            range = 0..59
        )
    }
}