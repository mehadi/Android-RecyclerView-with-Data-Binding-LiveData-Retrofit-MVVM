package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.components.UserSearchField
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListEmpty
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListError
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListFavoritesEmpty
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListItem
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListLoading
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListSearchEmpty

@Composable
fun UserListRoute(
    onUserClick: (Int) -> Unit,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UserListScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onUserClick = onUserClick,
        onErrorShown = viewModel::dismissError,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSortOrderChange = viewModel::onSortOrderChange,
        onFavoritesOnlyChange = viewModel::onFavoritesOnlyChange,
        onToggleFavorite = viewModel::onToggleFavorite,
    )
}

/** Stateless and self-contained so it can be rendered directly in tests with fixture states. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    uiState: UserListUiState,
    onRefresh: () -> Unit,
    onUserClick: (Int) -> Unit,
    onErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (String) -> Unit = {},
    onSortOrderChange: (UserSortOrder) -> Unit = {},
    onFavoritesOnlyChange: (Boolean) -> Unit = {},
    onToggleFavorite: (User) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val hasCachedUsers = uiState.users.isNotEmpty()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // A refresh failure is shown as a dismissible snackbar when we still have cached data to
    // display; only an empty cache escalates to the full-screen error state below.
    val errorMessage = uiState.errorMessageRes?.let { stringResource(it) }
    LaunchedEffect(errorMessage, hasCachedUsers) {
        if (errorMessage != null && hasCachedUsers) {
            snackbarHostState.showSnackbar(errorMessage)
            onErrorShown()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    // Sorting/filtering an empty or still-loading cache is meaningless.
                    if (hasCachedUsers) {
                        SortFilterMenuAction(
                            sortOrder = uiState.sortOrder,
                            favoritesOnly = uiState.favoritesOnly,
                            onSortOrderChange = onSortOrderChange,
                            onFavoritesOnlyChange = onFavoritesOnlyChange,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Search only makes sense once there is something cached to filter; it stays
            // pinned above the list/pull-to-refresh area instead of scrolling away with it.
            if (hasCachedUsers) {
                UserSearchField(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> UserListLoading(modifier = Modifier.fillMaxSize())

                    hasCachedUsers ->
                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = onRefresh,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            when {
                                uiState.filteredUsers.isNotEmpty() ->
                                    UserList(
                                        users = uiState.filteredUsers,
                                        onUserClick = onUserClick,
                                        onToggleFavorite = onToggleFavorite,
                                    )

                                // The favorites filter (not the search) is what emptied the
                                // list, so explain how to favorite someone instead of showing
                                // the generic "no matches" search state.
                                uiState.favoritesOnly && uiState.searchQuery.isBlank() ->
                                    UserListFavoritesEmpty(modifier = Modifier.fillMaxSize())

                                else ->
                                    UserListSearchEmpty(
                                        query = uiState.searchQuery,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                            }
                        }

                    errorMessage != null ->
                        UserListError(
                            message = errorMessage,
                            onRetry = onRefresh,
                            modifier = Modifier.fillMaxSize(),
                        )

                    else -> UserListEmpty(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

/**
 * Top-app-bar action opening the session-only sort/filter menu. The icon is tinted primary while
 * the favorites filter is active so the narrowed list is never mistaken for the full one.
 */
@Composable
private fun SortFilterMenuAction(
    sortOrder: UserSortOrder,
    favoritesOnly: Boolean,
    onSortOrderChange: (UserSortOrder) -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(R.string.sort_filter_menu),
                tint =
                    if (favoritesOnly) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        LocalContentColor.current
                    },
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )

            UserSortOrder.entries.forEach { order ->
                DropdownMenuItem(
                    text = { Text(stringResource(order.labelRes())) },
                    leadingIcon = {
                        // Selection handling lives on the whole menu item; a null callback keeps
                        // the radio purely visual so TalkBack announces one node, not two.
                        RadioButton(selected = sortOrder == order, onClick = null)
                    },
                    onClick = {
                        onSortOrderChange(order)
                        expanded = false
                    },
                )
            }

            HorizontalDivider()

            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_favorites_only)) },
                leadingIcon = {
                    Checkbox(checked = favoritesOnly, onCheckedChange = null)
                },
                onClick = {
                    onFavoritesOnlyChange(!favoritesOnly)
                    expanded = false
                },
            )
        }
    }
}

private fun UserSortOrder.labelRes(): Int =
    when (this) {
        UserSortOrder.NAME -> R.string.sort_by_name
        UserSortOrder.USERNAME -> R.string.sort_by_username
        UserSortOrder.COMPANY -> R.string.sort_by_company
    }

@Composable
private fun UserList(
    users: List<User>,
    onUserClick: (Int) -> Unit,
    onToggleFavorite: (User) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm + Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm + Spacing.xs),
    ) {
        items(users, key = { it.id }) { user ->
            UserListItem(
                user = user,
                onClick = { onUserClick(user.id) },
                onToggleFavorite = { onToggleFavorite(user) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}
