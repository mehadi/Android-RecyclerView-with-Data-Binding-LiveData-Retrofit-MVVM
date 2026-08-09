package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.common.filterUsers
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.common.toMessageRes
import javax.inject.Inject

@HiltViewModel
class UserListViewModel
    @Inject
    constructor(
        observeUsersUseCase: ObserveUsersUseCase,
        private val refreshUsersUseCase: RefreshUsersUseCase,
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(UserListUiState())
        val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

        /** Client-side list controls; combined with the cached user list below so none of them
         *  ever touch the network and only re-run the (cheap) filter/sort per change. */
        private val searchQueryFlow = MutableStateFlow("")
        private val sortOrderFlow = MutableStateFlow(UserSortOrder.NAME)
        private val favoritesOnlyFlow = MutableStateFlow(false)

        init {
            viewModelScope.launch {
                combine(
                    observeUsersUseCase(),
                    searchQueryFlow,
                    sortOrderFlow,
                    favoritesOnlyFlow,
                ) { users, query, sortOrder, favoritesOnly ->
                    ListControls(users, query, sortOrder, favoritesOnly)
                }.collect { (users, query, sortOrder, favoritesOnly) ->
                    _uiState.update {
                        it.copy(
                            users = users,
                            searchQuery = query,
                            sortOrder = sortOrder,
                            favoritesOnly = favoritesOnly,
                            filteredUsers = visibleUsers(users, query, sortOrder, favoritesOnly),
                        )
                    }
                }
            }
            refresh()
        }

        fun refresh() {
            viewModelScope.launch {
                val hasCachedUsers = _uiState.value.users.isNotEmpty()
                _uiState.update {
                    it.copy(isLoading = !hasCachedUsers, isRefreshing = hasCachedUsers, errorMessageRes = null)
                }

                refreshUsersUseCase().onFailure { error ->
                    _uiState.update { it.copy(errorMessageRes = error.toMessageRes()) }
                }

                _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            }
        }

        fun dismissError() {
            _uiState.update { it.copy(errorMessageRes = null) }
        }

        fun onSearchQueryChange(query: String) {
            searchQueryFlow.value = query
        }

        fun onSortOrderChange(sortOrder: UserSortOrder) {
            sortOrderFlow.value = sortOrder
        }

        fun onFavoritesOnlyChange(favoritesOnly: Boolean) {
            favoritesOnlyFlow.value = favoritesOnly
        }

        fun onToggleFavorite(user: User) {
            viewModelScope.launch {
                toggleFavoriteUseCase(user.id, !user.isFavorite)
            }
        }

        /** Search, favorites filter, and sort applied together, all client-side (the data set is tiny). */
        private fun visibleUsers(
            users: List<User>,
            query: String,
            sortOrder: UserSortOrder,
            favoritesOnly: Boolean,
        ): List<User> {
            val matching =
                filterUsers(users, query)
                    .let { if (favoritesOnly) it.filter(User::isFavorite) else it }
            return when (sortOrder) {
                UserSortOrder.NAME ->
                    matching.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.displayName })
                UserSortOrder.USERNAME ->
                    matching.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.username })
                UserSortOrder.COMPANY ->
                    matching.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.company })
            }
        }

        /** Bundles the four [combine] sources so the collector can destructure them by name. */
        private data class ListControls(
            val users: List<User>,
            val query: String,
            val sortOrder: UserSortOrder,
            val favoritesOnly: Boolean,
        )
    }
