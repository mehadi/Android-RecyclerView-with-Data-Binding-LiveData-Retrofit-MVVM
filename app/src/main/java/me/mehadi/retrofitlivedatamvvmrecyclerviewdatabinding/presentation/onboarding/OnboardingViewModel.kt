package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    /** Persists that onboarding has been completed so it is never shown again. */
    fun onOnboardingFinished() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingSeen(true)
        }
    }
}
