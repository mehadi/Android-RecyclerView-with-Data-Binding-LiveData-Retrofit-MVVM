package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveFavoriteUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.common.filterUsers
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel
    @Inject
    constructor(
        observeFavoriteUsersUseCase: ObserveFavoriteUsersUseCase,
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(FavoritesUiState())
        val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

        /** Client-side search query; combined with the favorites flow below so filtering never
         *  touches the database and only re-runs the (cheap) filter itself per keystroke. */
        private val searchQueryFlow = MutableStateFlow("")

        init {
            viewModelScope.launch {
                combine(observeFavoriteUsersUseCase(), searchQueryFlow) { favorites, query ->
                    favorites to query
                }.collect { (favorites, query) ->
                    _uiState.update {
                        it.copy(
                            favorites = favorites,
                            searchQuery = query,
                            filteredFavorites = filterUsers(favorites, query),
                            isLoading = false,
                        )
                    }
                }
            }
        }

        fun onSearchQueryChange(query: String) {
            searchQueryFlow.value = query
        }

        /**
         * Un-favoriting from this screen drops the user out of the list on the next emission, so
         * the removal is also remembered to offer an undo. Rapid successive removals simply
         * overwrite [FavoritesUiState.removedUserId]: only the latest one can be undone.
         */
        fun toggleFavorite(
            id: Int,
            isFavorite: Boolean,
        ) {
            viewModelScope.launch {
                toggleFavoriteUseCase(id, isFavorite)
            }
            if (!isFavorite) {
                _uiState.update { it.copy(removedUserId = id) }
            }
        }

        /** Re-favorites the most recently removed user ([ToggleFavoriteUseCase] is idempotent). */
        fun undoRemoval() {
            val removedId = _uiState.value.removedUserId ?: return
            _uiState.update { it.copy(removedUserId = null) }
            viewModelScope.launch {
                toggleFavoriteUseCase(removedId, true)
            }
        }

        /** The undo snackbar was dismissed without action; forget the pending removal. */
        fun onRemovalMessageShown() {
            _uiState.update { it.copy(removedUserId = null) }
        }
    }
