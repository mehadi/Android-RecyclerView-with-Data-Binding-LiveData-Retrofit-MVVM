package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

/**
 * Thin wrapper around the Preferences DataStore backing app settings. Deals only in raw
 * primitives/strings — enum parsing and defaults live in the repository that consumes this.
 */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("dynamic_color_enabled")
        val ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
    }

    val themeMode: Flow<String?> = context.userPreferencesDataStore.data.map { it[Keys.THEME_MODE] }

    val dynamicColorEnabled: Flow<Boolean?> =
        context.userPreferencesDataStore.data.map { it[Keys.DYNAMIC_COLOR_ENABLED] }

    val onboardingSeen: Flow<Boolean?> =
        context.userPreferencesDataStore.data.map { it[Keys.ONBOARDING_SEEN] }

    suspend fun setThemeMode(themeMode: String) {
        context.userPreferencesDataStore.edit { it[Keys.THEME_MODE] = themeMode }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.DYNAMIC_COLOR_ENABLED] = enabled }
    }

    suspend fun setOnboardingSeen(seen: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.ONBOARDING_SEEN] = seen }
    }
}
