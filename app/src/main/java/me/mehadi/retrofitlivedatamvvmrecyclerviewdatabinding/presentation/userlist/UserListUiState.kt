package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import androidx.annotation.StringRes
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

data class UserListUiState(
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    // Session-only list controls; deliberately reset when the ViewModel is recreated.
    val sortOrder: UserSortOrder = UserSortOrder.NAME,
    val favoritesOnly: Boolean = false,
    // Defaults to the unfiltered list so fixture states that only set [users] (e.g. tests,
    // previews) render correctly without also having to derive this themselves.
    val filteredUsers: List<User> = users,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
