package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.BuildConfig
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ClearCacheUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUsersUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(appVersion = BuildConfig.VERSION_NAME))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.themeMode,
                userPreferencesRepository.dynamicColorEnabled,
            ) { themeMode, dynamicColorEnabled -> themeMode to dynamicColorEnabled }
                .collect { (themeMode, dynamicColorEnabled) ->
                    _uiState.update {
                        it.copy(themeMode = themeMode, dynamicColorEnabled = dynamicColorEnabled)
                    }
                }
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { userPreferencesRepository.setThemeMode(themeMode) }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferencesRepository.setDynamicColorEnabled(enabled) }
    }

    fun onClearCacheClick() {
        _uiState.update { it.copy(showClearCacheConfirmation = true) }
    }

    fun onClearCacheDismiss() {
        _uiState.update { it.copy(showClearCacheConfirmation = false) }
    }

    /** Wipes the local cache, then immediately re-fetches from the network so the list isn't left empty. */
    fun onClearCacheConfirm() {
        _uiState.update { it.copy(showClearCacheConfirmation = false, isClearingCache = true) }
        viewModelScope.launch {
            clearCacheUseCase()
            refreshUsersUseCase()
            _uiState.update { it.copy(isClearingCache = false) }
        }
    }
}
