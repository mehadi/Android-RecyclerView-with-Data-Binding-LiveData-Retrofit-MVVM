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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListEmpty
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListError
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
    onSearchQueryChange: (String) -> Unit = {},
    onToggleFavorite: (User) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val hasCachedUsers = uiState.users.isNotEmpty()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // A refresh failure is shown as a dismissible snackbar when we still have cached data to
    // display; only an empty cache escalates to the full-screen error state below.
    LaunchedEffect(uiState.errorMessage, hasCachedUsers) {
        val message = uiState.errorMessage
        if (message != null && hasCachedUsers) {
            snackbarHostState.showSnackbar(message)
            onErrorShown()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> UserListLoading(modifier = Modifier.fillMaxSize())

                    hasCachedUsers -> PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        if (uiState.filteredUsers.isEmpty()) {
                            UserListSearchEmpty(
                                query = uiState.searchQuery,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            UserList(
                                users = uiState.filteredUsers,
                                onUserClick = onUserClick,
                                onToggleFavorite = onToggleFavorite,
                            )
                        }
                    }

                    uiState.errorMessage != null -> UserListError(
                        message = uiState.errorMessage,
                        onRetry = onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    )

                    else -> UserListEmpty(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
private fun UserSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text(stringResource(R.string.search_users_placeholder)) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.search_users_clear),
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
    )
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
