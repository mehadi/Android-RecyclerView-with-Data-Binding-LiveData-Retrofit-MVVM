package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository

class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    private val dynamicColorEnabledFlow = MutableStateFlow(true)
    private val onboardingSeenFlow = MutableStateFlow(false)

    override val themeMode: Flow<ThemeMode> = themeModeFlow
    override val dynamicColorEnabled: Flow<Boolean> = dynamicColorEnabledFlow
    override val onboardingSeen: Flow<Boolean> = onboardingSeenFlow

    val currentThemeMode: ThemeMode get() = themeModeFlow.value
    val currentDynamicColorEnabled: Boolean get() = dynamicColorEnabledFlow.value
    val currentOnboardingSeen: Boolean get() = onboardingSeenFlow.value

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        themeModeFlow.value = themeMode
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dynamicColorEnabledFlow.value = enabled
    }

    override suspend fun setOnboardingSeen(seen: Boolean) {
        onboardingSeenFlow.value = seen
    }
}
