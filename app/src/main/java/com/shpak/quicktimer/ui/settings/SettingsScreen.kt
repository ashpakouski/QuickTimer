package com.shpak.quicktimer.ui.settings

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shpak.quicktimer.R
import com.shpak.quicktimer.core.designsystem.theme.QuickTimerTheme
import com.shpak.quicktimer.domain.alarm.AlarmSound

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        viewModel.stopPreview()
    }

    SettingsScreen(
        uiState = uiState,
        onSoundSelect = viewModel::onSoundSelect,
        onPreviewToggle = viewModel::onPreviewToggle,
        onNavigateUp = onNavigateUp
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onSoundSelect: (AlarmSound) -> Unit,
    onPreviewToggle: (AlarmSound) -> Unit,
    onNavigateUp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp,
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        SettingsContent(
            uiState = uiState,
            onSoundSelect = onSoundSelect,
            onPreviewToggle = onPreviewToggle,
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onSoundSelect: (AlarmSound) -> Unit,
    onPreviewToggle: (AlarmSound) -> Unit,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            )
    ) {
        SettingsSection(
            title = stringResource(R.string.settings_section_sound)
        ) {
            SoundList(
                selected = uiState.selectedSound,
                previewing = uiState.previewingSound,
                onSelect = onSoundSelect,
                onPreviewToggle = onPreviewToggle
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
        content()
    }
}

private val SoundListSpacing = 4.dp
private val SoundPreviewButtonSize = 48.dp
private val SoundRowMinHeight = SoundPreviewButtonSize + SoundListSpacing * 2
private val SoundRowShape = RoundedCornerShape(SoundPreviewButtonSize / 2 + SoundListSpacing)
private val SoundListShape = RoundedCornerShape(SoundPreviewButtonSize / 2 + SoundListSpacing * 2)

@Composable
private fun SoundList(
    selected: AlarmSound?,
    previewing: AlarmSound?,
    onSelect: (AlarmSound) -> Unit,
    onPreviewToggle: (AlarmSound) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SoundListSpacing),
        modifier = Modifier
            .fillMaxWidth()
            .clip(SoundListShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(SoundListSpacing)
            .selectableGroup()
    ) {
        AlarmSound.entries.forEach { sound ->
            SoundRow(
                sound = sound,
                isSelected = sound == selected,
                isPreviewing = sound == previewing,
                onSelect = {
                    onSelect(sound)
                },
                onPreviewToggle = {
                    onPreviewToggle(sound)
                }
            )
        }
    }
}

@Composable
private fun SoundRow(
    sound: AlarmSound,
    isSelected: Boolean,
    isPreviewing: Boolean,
    onSelect: () -> Unit,
    onPreviewToggle: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme
    val colorSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Color>()
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = colorSpec,
        label = "soundRowContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.onPrimaryContainer
        } else {
            colorScheme.onSurface
        },
        animationSpec = colorSpec,
        label = "soundRowContent"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(SoundRowShape)
            .background(containerColor)
            .padding(
                end = SoundListSpacing
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .heightIn(min = SoundRowMinHeight)
                .selectable(
                    selected = isSelected,
                    role = Role.RadioButton,
                    onClick = {
                        if (!isSelected) {
                            haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        }
                        onSelect()
                    }
                )
                .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null
            )

            Text(
                text = stringResource(sound.labelId),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
        }

        SoundPreviewButton(
            isPreviewing = isPreviewing,
            onToggle = onPreviewToggle
        )
    }
}

@Composable
private fun SoundPreviewButton(
    isPreviewing: Boolean,
    onToggle: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme

    FilledIconToggleButton(
        checked = isPreviewing,
        onCheckedChange = { isChecked ->
            haptics.performHapticFeedback(
                if (isChecked) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
            )
            onToggle()
        },
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconToggleButtonColors(
            containerColor = Color.Transparent,
            contentColor = colorScheme.onSurfaceVariant,
            checkedContainerColor = colorScheme.primary,
            checkedContentColor = colorScheme.onPrimary
        ),
        modifier = Modifier.size(SoundPreviewButtonSize)
    ) {
        Icon(
            imageVector = if (isPreviewing) {
                Icons.Rounded.Stop
            } else {
                Icons.Rounded.PlayArrow
            },
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsScreenPreview(darkTheme: Boolean = false) =
    QuickTimerTheme(darkTheme = darkTheme) {
        SettingsScreen(
            uiState = SettingsUiState(previewingSound = AlarmSound.CHIME),
            onSoundSelect = {},
            onPreviewToggle = {},
            onNavigateUp = {}
        )
    }

@Preview(showBackground = true, widthDp = 400, heightDp = 844)
@Composable
private fun SettingsScreenLightPreview() = SettingsScreenPreview()

@Preview(
    showBackground = true,
    widthDp = 400,
    heightDp = 844,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SettingsScreenDarkPreview() = SettingsScreenPreview(darkTheme = true)