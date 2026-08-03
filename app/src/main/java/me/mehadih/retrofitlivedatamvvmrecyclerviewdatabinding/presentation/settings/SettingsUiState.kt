package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings

import android.os.Build
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode

/**
 * UI state for [SettingsScreen]. Kept flat and Parcelable-free since the screen is always
 * re-derived from [me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository]
 * on process restart.
 */
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColorEnabled: Boolean = false,
    val isDynamicColorSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    val isClearingCache: Boolean = false,
    val showClearCacheConfirmation: Boolean = false,
    val appVersion: String = "",
)
