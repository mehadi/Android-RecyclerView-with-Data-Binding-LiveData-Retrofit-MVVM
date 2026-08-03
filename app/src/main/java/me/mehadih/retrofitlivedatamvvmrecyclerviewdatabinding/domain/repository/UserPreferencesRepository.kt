package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode

/** Single source of truth for user-configurable app settings, persisted locally on-device. */
interface UserPreferencesRepository {
    val themeMode: Flow<ThemeMode>

    val dynamicColorEnabled: Flow<Boolean>

    val onboardingSeen: Flow<Boolean>

    suspend fun setThemeMode(themeMode: ThemeMode)

    suspend fun setDynamicColorEnabled(enabled: Boolean)

    suspend fun setOnboardingSeen(seen: Boolean)
}
