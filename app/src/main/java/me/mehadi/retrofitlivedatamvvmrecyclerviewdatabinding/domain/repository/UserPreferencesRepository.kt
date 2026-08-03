package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode

/**
 * Single source of truth for user-configurable app settings, persisted locally on-device.
 *
 * Architecture note: ViewModels (see [me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings.SettingsViewModel],
 * [me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.onboarding.OnboardingViewModel],
 * [me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.MainActivityViewModel])
 * are allowed to inject this repository directly instead of going through a use case. These reads/writes
 * are simple one-line preference get/set calls with no business rule to encapsulate, so the use-case layer
 * is intentionally skipped here. Multi-step or business-rule operations (e.g. favorites, cache) still go
 * through a use case.
 */
interface UserPreferencesRepository {
    val themeMode: Flow<ThemeMode>

    val dynamicColorEnabled: Flow<Boolean>

    val onboardingSeen: Flow<Boolean>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setDynamicColorEnabled(enabled: Boolean)

    suspend fun setOnboardingSeen(seen: Boolean)
}
