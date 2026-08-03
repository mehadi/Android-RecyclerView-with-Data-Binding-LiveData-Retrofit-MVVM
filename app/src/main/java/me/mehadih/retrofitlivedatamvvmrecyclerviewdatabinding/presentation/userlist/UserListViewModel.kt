package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.GetUsersUseCase
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUsersUseCase
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    /** Client-side search query; combined with the cached user list below so filtering never
     *  touches the network and only re-runs the (cheap) filter itself per keystroke. */
    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(getUsersUseCase(), _searchQuery) { users, query -> users to query }
                .collect { (users, query) ->
                    _uiState.update {
                        it.copy(
                            users = users,
                            searchQuery = query,
                            filteredUsers = filterUsers(users, query),
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
                it.copy(isLoading = !hasCachedUsers, isRefreshing = hasCachedUsers, errorMessage = null)
            }

            refreshUsersUseCase().onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message ?: "Couldn't refresh users. Showing the last saved list.")
                }
            }

            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onToggleFavorite(user: User) {
        viewModelScope.launch {
            toggleFavoriteUseCase(user.id, !user.isFavorite)
        }
    }

    private fun filterUsers(users: List<User>, query: String): List<User> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return users
        return users.filter { user ->
            user.name.contains(trimmed, ignoreCase = true) ||
                user.username.contains(trimmed, ignoreCase = true) ||
                user.email.contains(trimmed, ignoreCase = true)
        }
    }
}
