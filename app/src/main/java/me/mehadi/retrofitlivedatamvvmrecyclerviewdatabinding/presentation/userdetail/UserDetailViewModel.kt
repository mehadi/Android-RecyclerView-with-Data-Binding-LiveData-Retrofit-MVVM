package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveAlbumsForUserUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObservePostsForUserUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveTodosForUserUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveUserByIdUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUserContentUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.common.toMessageRes
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.NavArgs
import javax.inject.Inject

@HiltViewModel
class UserDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeUserByIdUseCase: ObserveUserByIdUseCase,
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
        observePostsForUserUseCase: ObservePostsForUserUseCase,
        observeTodosForUserUseCase: ObserveTodosForUserUseCase,
        observeAlbumsForUserUseCase: ObserveAlbumsForUserUseCase,
        private val refreshUserContentUseCase: RefreshUserContentUseCase,
    ) : ViewModel() {
        private val userId: Int =
            checkNotNull(savedStateHandle[NavArgs.USER_ID]) {
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
            viewModelScope.launch {
                combine(
                    observePostsForUserUseCase(userId),
                    observeTodosForUserUseCase(userId),
                    observeAlbumsForUserUseCase(userId),
                ) { posts, todos, albums -> Triple(posts, todos, albums) }
                    .collect { (posts, todos, albums) ->
                        _uiState.update { it.copy(posts = posts, todos = todos, albums = albums) }
                    }
            }
            refreshContent()
        }

        /** Flips the current user's favorite flag. The UI reflects the change once the
         *  repository emits the updated user through [observeUserByIdUseCase]. */
        fun onToggleFavorite() {
            val currentUser = _uiState.value.user ?: return
            viewModelScope.launch {
                toggleFavoriteUseCase(currentUser.id, !currentUser.isFavorite)
            }
        }

        fun retryContent() {
            refreshContent()
        }

        fun onContentErrorShown() {
            _uiState.update { it.copy(contentErrorRes = null) }
        }

        /** Refreshes this user's posts/todos/albums. Mirrors the list screen's graduated errors:
         *  a failure with cached content present surfaces as a dismissible snackbar, while an
         *  empty cache escalates to the inline error section with a Retry action. */
        private fun refreshContent() {
            viewModelScope.launch {
                val hasCachedContent = _uiState.value.hasContent
                _uiState.update { it.copy(isContentLoading = !hasCachedContent, contentErrorRes = null) }

                refreshUserContentUseCase(userId).onFailure { error ->
                    _uiState.update { it.copy(contentErrorRes = error.toMessageRes()) }
                }

                _uiState.update { it.copy(isContentLoading = false) }
            }
        }
    }
