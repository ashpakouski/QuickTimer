package com.shpak.quicktimer.core.designsystem.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.shpak.quicktimer.core.designsystem.theme.TimerTextStyles
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier,
    colors: NumberPickerColors = NumberPickerDefaults.colors(),
    rowHeight: Dp = 64.dp,
    label: (Int) -> String = ::twoDigits
) {
    val valuesCount = range.last - range.first + 1
    val sideRowsCount = 2
    val visibleRows = sideRowsCount * 2 + 1
    val totalCount = valuesCount * 1000
    val startIndex = (totalCount / 2 / valuesCount) * valuesCount
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = startIndex + (value - range.first).coerceIn(0..<valuesCount)
    )
    val rowHeightPx = with(LocalDensity.current) {
        rowHeight.toPx()
    }
    val centeredIndex by remember(state, rowHeightPx, totalCount) {
        derivedStateOf { state.centeredIndex(rowHeightPx, totalCount) }
    }
    var isAutoscrolling by remember { mutableStateOf(false) }
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val scope = rememberCoroutineScope()
    val isDragged by state.interactionSource.collectIsDraggedAsState()
    val haptics = LocalHapticFeedback.current

    val motionScheme = MaterialTheme.motionScheme
    val scrollSpec = motionScheme.defaultSpatialSpec<Float>()
    val snapLayoutInfoProvider = remember(state) {
        SnapLayoutInfoProvider(state, SnapPosition.Center)
    }
    val decaySpec = rememberSplineBasedDecay<Float>()
    val flingBehavior = remember(snapLayoutInfoProvider, decaySpec, scrollSpec) {
        snapFlingBehavior(snapLayoutInfoProvider, decaySpec, scrollSpec)
    }

    LaunchedEffect(state, range) {
        snapshotFlow {
            centeredIndex.takeUnless { isAutoscrolling }
        }
            .filterNotNull()
            .distinctUntilChanged()
            .collect { index ->
                val selectedNumber = range.first + index % valuesCount
                if (selectedNumber != currentValue) {
                    currentOnValueChange(selectedNumber)
                }
            }
    }

    LaunchedEffect(state) {
        snapshotFlow { centeredIndex }
            .drop(1)
            .collect {
                if (!isAutoscrolling) {
                    haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                }
            }
    }

    LaunchedEffect(value, range) {
        val target = (value - range.first).coerceIn(0..<valuesCount)
        val current = centeredIndex % valuesCount
        if (target != current && !state.isScrollInProgress) {
            var delta = target - current
            if (delta > valuesCount / 2) {
                delta -= valuesCount
            }
            if (delta < -valuesCount / 2) {
                delta += valuesCount
            }
            isAutoscrolling = true
            try {
                state.animateScrollToItem((centeredIndex + delta).coerceIn(0..<totalCount))
            } finally {
                isAutoscrolling = false
            }
        }
    }

    val pillCorner by animateDpAsState(
        targetValue = if (isDragged) 18.dp else 26.dp,
        animationSpec = motionScheme.fastSpatialSpec(),
        label = "pillCorner"
    )
    val pillScale by animateFloatAsState(
        targetValue = if (isDragged) 0.98f else 1f,
        animationSpec = motionScheme.fastSpatialSpec(),
        label = "pillScale"
    )

    val fadeFraction = 0.12f
    Box(
        modifier = modifier
            .height(rowHeight * visibleRows)
            .background(colors.containerColor)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(rowHeight + 10.dp)
                .graphicsLayer {
                    scaleX = pillScale
                    scaleY = pillScale
                }
                .clip(RoundedCornerShape(pillCorner))
                .background(colors.selectionColor)
        )

        LazyColumn(
            state = state,
            flingBehavior = flingBehavior,
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
            items(count = totalCount) { index ->
                val text = label(range.first + index % valuesCount)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .clickable(
                            enabled = state.distanceFromCenter(index, rowHeightPx) <= 1.1f
                        ) {
                            scope.launch {
                                state.animateScrollToItem(index)
                            }
                        }
                        .graphicsLayer {
                            val distance = state.distanceFromCenter(index, rowHeightPx)
                            alpha = alphaForDistance(distance)
                            val scale = scaleForDistance(distance)
                            scaleX = scale
                            scaleY = scale
                        }
                ) {
                    Text(
                        text = text,
                        color = colors.contentColor,
                        style = TimerTextStyles.wheelUnselected,
                        maxLines = 1,
                        autoSize = WheelTextAutoSize,
                        modifier = Modifier.graphicsLayer {
                            alpha = 1f - selectedTextAlphaForDistance(
                                state.distanceFromCenter(index, rowHeightPx)
                            )
                        }
                    )
                    Text(
                        text = text,
                        color = colors.selectedContentColor,
                        style = TimerTextStyles.wheelSelected,
                        maxLines = 1,
                        autoSize = WheelTextAutoSize,
                        modifier = Modifier.graphicsLayer {
                            alpha = selectedTextAlphaForDistance(
                                state.distanceFromCenter(index, rowHeightPx)
                            )
                        }
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

/**
 * How far the row is from the center slot.
 */
private fun LazyListState.distanceFromCenter(index: Int, rowHeightPx: Float): Float {
    if (rowHeightPx <= 0f) {
        return 0f
    }

    val centerPosition = firstVisibleItemIndex + firstVisibleItemScrollOffset / rowHeightPx

    return abs(index - centerPosition)
}

private fun twoDigits(value: Int): String = value.toString().padStart(2, '0')

private fun alphaForDistance(distance: Float): Float = when {
    distance <= 1f -> 1f
    distance <= 2f -> lerp(1f, 0.42f, distance - 1f)
    else -> 0.42f
}

private fun scaleForDistance(distance: Float): Float = when {
    distance <= 1f -> lerp(1f, AdjacentRowScale, distance)
    distance <= 2f -> lerp(AdjacentRowScale, OuterRowScale, distance - 1f)
    else -> OuterRowScale
}

private fun selectedTextAlphaForDistance(distance: Float): Float = (1f - distance).coerceIn(0f, 1f)

private val AdjacentRowScale = 33f / TimerTextStyles.wheelSelected.fontSize.value
private val OuterRowScale = 31f / TimerTextStyles.wheelSelected.fontSize.value

// Shrinks numerals that would overflow their row at large font scales.
private val WheelTextAutoSize = TextAutoSize.StepBased(
    maxFontSize = TimerTextStyles.wheelSelected.fontSize
)

@Immutable
data class NumberPickerColors(
    val containerColor: Color,
    val selectionColor: Color,
    val contentColor: Color,
    val selectedContentColor: Color
)

object NumberPickerDefaults {
    @Composable
    fun colors(
        containerColor: Color = Color.Transparent,
        selectionColor: Color = MaterialTheme.colorScheme.primaryContainer,
        contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
    ): NumberPickerColors = NumberPickerColors(
        containerColor = containerColor,
        selectionColor = selectionColor,
        contentColor = contentColor,
        selectedContentColor = selectedContentColor
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