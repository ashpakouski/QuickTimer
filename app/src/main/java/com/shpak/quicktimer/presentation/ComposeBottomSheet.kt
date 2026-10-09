package com.shpak.quicktimer.presentation

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.view.Window
import androidx.activity.ComponentDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.WindowCompat
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme

abstract class ComposeBottomSheet(context: Context) : ComponentDialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

    protected fun setSheetContent(content: @Composable () -> Unit) {
        setContentView(
            ComposeView(context).apply {
                setContent {
                    QuickTimerTheme {
                        BottomSheetLayout(
                            onDismissRequest = ::cancel,
                            content = content
                        )
                    }
                }
            }
        )

        window?.setUpAsBottomSheet()
    }
}

// ModalBottomSheet's internal swipe thresholds.
// private val PositionalThreshold = 56.dp
// private val VelocityThreshold = 125.dp

// private enum class SheetAnchor { Expanded, Hidden }

// Mirrors ModalBottomSheet's look through its public defaults, and its swipe-to-dismiss.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetLayout(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    // val sheetState = remember { AnchoredDraggableState(SheetAnchor.Expanded) }
    // val nestedScrollConnection = rememberSwipeToDismissConnection(sheetState)

    // LaunchedEffect(sheetState) {
    //     snapshotFlow { sheetState.currentValue }
    //         .filter { value -> value == SheetAnchor.Hidden }
    //         .collect { onDismissRequest() }
    // }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onDismissRequest
                )
        )

        Surface(
            shape = BottomSheetDefaults.ExpandedShape,
            color = BottomSheetDefaults.ContainerColor,
            modifier = Modifier
                .statusBarsPadding()
                .widthIn(max = BottomSheetDefaults.SheetMaxWidth)
                .fillMaxWidth()
                // .onSizeChanged { size ->
                //     sheetState.updateAnchors(
                //         DraggableAnchors {
                //             SheetAnchor.Expanded at 0f
                //             SheetAnchor.Hidden at size.height.toFloat()
                //         }
                //     )
                // }
                // .offset {
                //     val offset = sheetState.offset.takeUnless { value -> value.isNaN() } ?: 0f
                //     IntOffset(x = 0, y = offset.roundToInt())
                // }
                // .nestedScroll(nestedScrollConnection)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
            ) {
                // BottomSheetDefaults.DragHandle()
                content()
            }
        }
    }
}

/*
@Composable
private fun rememberSwipeToDismissConnection(
    sheetState: AnchoredDraggableState<SheetAnchor>
): NestedScrollConnection {
    val density = LocalDensity.current
    val animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()

    return remember(sheetState, density, animationSpec) {
        val positionalThreshold = with(density) { PositionalThreshold.toPx() }
        val velocityThreshold = with(density) { VelocityThreshold.toPx() }

        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset =
                if (available.y < 0f && source == NestedScrollSource.UserInput) {
                    Offset(x = 0f, y = sheetState.dispatchRawDelta(available.y))
                } else {
                    Offset.Zero
                }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset =
                if (available.y != 0f && source == NestedScrollSource.UserInput) {
                    Offset(x = 0f, y = sheetState.dispatchRawDelta(available.y))
                } else {
                    Offset.Zero
                }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (sheetState.offset <= 0f) {
                    return Velocity.Zero
                }

                val target = when {
                    available.y > velocityThreshold -> SheetAnchor.Hidden
                    available.y < -velocityThreshold -> SheetAnchor.Expanded
                    sheetState.offset > positionalThreshold -> SheetAnchor.Hidden
                    else -> SheetAnchor.Expanded
                }
                sheetState.animateTo(target, animationSpec)

                return available
            }
        }
    }
}
 */

private fun Window.setUpAsBottomSheet() {
    setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    setWindowAnimations(android.R.style.Animation_InputMethod)

    WindowCompat.enableEdgeToEdge(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        attributes = attributes.apply {
            fitInsetsTypes = 0
        }
    }
}