package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onBack = onBack,
        onThemeModeSelected = viewModel::setThemeMode,
        onDynamicColorToggle = viewModel::setDynamicColorEnabled,
        onClearCacheClick = viewModel::onClearCacheClick,
        onClearCacheConfirm = viewModel::onClearCacheConfirm,
        onClearCacheDismiss = viewModel::onClearCacheDismiss,
    )
}

/** Stateless and self-contained so it can be rendered directly in tests with fixture states. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDynamicColorToggle: (Boolean) -> Unit,
    onClearCacheClick: () -> Unit,
    onClearCacheConfirm: () -> Unit,
    onClearCacheDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                AppearanceSection(
                    themeMode = uiState.themeMode,
                    dynamicColorEnabled = uiState.dynamicColorEnabled,
                    isDynamicColorSupported = uiState.isDynamicColorSupported,
                    onThemeModeSelected = onThemeModeSelected,
                    onDynamicColorToggle = onDynamicColorToggle,
                )

                HorizontalDivider()

                DataSection(
                    isClearingCache = uiState.isClearingCache,
                    onClearCacheClick = onClearCacheClick,
                )

                HorizontalDivider()

                AboutSection(appVersion = uiState.appVersion)
            }

            if (uiState.showClearCacheConfirmation) {
                ClearCacheConfirmationDialog(
                    onConfirm = onClearCacheConfirm,
                    onDismiss = onClearCacheDismiss,
                )
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    themeMode: ThemeMode,
    dynamicColorEnabled: Boolean,
    isDynamicColorSupported: Boolean,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDynamicColorToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(stringResource(R.string.settings_section_appearance))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_theme_label)) },
            leadingContent = {
                Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
            },
            supportingContent = {
                ThemeModePicker(
                    selected = themeMode,
                    onSelected = onThemeModeSelected,
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            },
        )

        // Material You dynamic color is only available on Android 12 (API 31) and above.
        if (isDynamicColorSupported) {
            ListItem(
                // The switch alone would announce as an unlabeled "Switch, on/off" to TalkBack.
                // Making the whole row toggleable merges the headline/description text and the
                // switch's checked state into a single, correctly-labeled accessibility node, and
                // also grows the touch target from the 40dp switch to the full-width row.
                modifier = Modifier.toggleable(
                    value = dynamicColorEnabled,
                    onValueChange = onDynamicColorToggle,
                    role = Role.Switch,
                ),
                headlineContent = { Text(stringResource(R.string.settings_dynamic_color_label)) },
                supportingContent = { Text(stringResource(R.string.settings_dynamic_color_description)) },
                leadingContent = {
                    Icon(imageVector = Icons.Outlined.WbSunny, contentDescription = null)
                },
                trailingContent = {
                    // Click handling now lives on the row's toggleable modifier above; passing
                    // null here avoids double-announcing/double-handling the toggle.
                    Switch(checked = dynamicColorEnabled, onCheckedChange = null)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModePicker(
    selected: ThemeMode,
    onSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)

    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                label = { Text(stringResource(mode.labelRes())) },
            )
        }
    }
}

private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}

@Composable
private fun DataSection(
    isClearingCache: Boolean,
    onClearCacheClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(stringResource(R.string.settings_section_data))

        ListItem(
            modifier = Modifier.clickable(enabled = !isClearingCache, onClick = onClearCacheClick),
            headlineContent = { Text(stringResource(R.string.settings_clear_cache_label)) },
            supportingContent = { Text(stringResource(R.string.settings_clear_cache_description)) },
            leadingContent = {
                Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = null)
            },
            trailingContent = {
                if (isClearingCache) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            },
        )
    }
}

@Composable
private fun AboutSection(
    appVersion: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(stringResource(R.string.settings_section_about))

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_version_label)) },
            supportingContent = { Text(appVersion) },
            leadingContent = {
                Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
            },
        )

        ListItem(
            headlineContent = { Text(stringResource(R.string.settings_about_attribution)) },
            leadingContent = {
                Icon(imageVector = Icons.Outlined.Public, contentDescription = null)
            },
        )
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
    )
}

@Composable
private fun ClearCacheConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_clear_cache_dialog_title)) },
        text = { Text(stringResource(R.string.settings_clear_cache_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.settings_clear_cache_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.settings_clear_cache_cancel))
            }
        },
    )
}
