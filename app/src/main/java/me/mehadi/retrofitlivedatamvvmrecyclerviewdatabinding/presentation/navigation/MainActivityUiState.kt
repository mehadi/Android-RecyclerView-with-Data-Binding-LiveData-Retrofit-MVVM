package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode

/**
 * Root-level UI state gating the very first frame drawn after the splash screen. [Loading] keeps
 * the splash screen up (see [MainActivityViewModel]/MainActivity) so neither the wrong theme nor
 * the wrong [AppNavHost] start destination is ever flashed on screen while preferences load.
 */
sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Ready(
        val themeMode: ThemeMode,
        val dynamicColorEnabled: Boolean,
        val onboardingSeen: Boolean,
    ) : MainActivityUiState
}
