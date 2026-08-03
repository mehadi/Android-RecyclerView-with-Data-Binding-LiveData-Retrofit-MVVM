package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveUserByIdUseCase
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.NavArgs
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeUserByIdUseCase: ObserveUserByIdUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[NavArgs.USER_ID]) {
        "UserDetailViewModel requires a '${NavArgs.USER_ID}' navigation argument"
    }

    private val _uiState = MutableStateFlow(UserDetailUiState())
    val uiState: StateFlow<UserDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeUserByIdUseCase(userId).collect { user ->
                _uiState.update { it.copy(user = user, isLoading = false) }
            }
        }
    }

    /** Flips the current user's favorite flag. The UI reflects the change once the
     *  repository emits the updated user through [observeUserByIdUseCase]. */
    fun onToggleFavorite() {
        val currentUser = _uiState.value.user ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentUser.id, !currentUser.isFavorite)
        }
    }
}
