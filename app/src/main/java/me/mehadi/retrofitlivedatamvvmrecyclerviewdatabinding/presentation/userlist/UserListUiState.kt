package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

data class UserListUiState(
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    // Defaults to the unfiltered list so fixture states that only set [users] (e.g. tests,
    // previews) render correctly without also having to derive this themselves.
    val filteredUsers: List<User> = users,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)
