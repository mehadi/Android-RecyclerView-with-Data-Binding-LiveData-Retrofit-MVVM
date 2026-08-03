package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Backs [me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.MainActivity]. Combines the
 * handful of preferences the app shell needs before it can draw its first real frame: the theme
 * to apply and whether onboarding has already been seen (which decides [AppNavHost]'s start
 * destination).
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = combine(
        userPreferencesRepository.themeMode,
        userPreferencesRepository.dynamicColorEnabled,
        userPreferencesRepository.onboardingSeen,
    ) { themeMode, dynamicColorEnabled, onboardingSeen ->
        MainActivityUiState.Ready(
            themeMode = themeMode,
            dynamicColorEnabled = dynamicColorEnabled,
            onboardingSeen = onboardingSeen,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState.Loading,
    )
}
