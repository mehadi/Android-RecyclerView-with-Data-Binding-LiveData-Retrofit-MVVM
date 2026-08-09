package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.components.UserSearchField
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListItem
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListSearchEmpty

@Composable
fun FavoritesRoute(
    onUserClick: (Int) -> Unit,
    onBrowseUsers: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FavoritesScreen(
        uiState = uiState,
        onUserClick = onUserClick,
        onBrowseUsers = onBrowseUsers,
        onToggleFavorite = viewModel::toggleFavorite,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onUndoRemoval = viewModel::undoRemoval,
        onRemovalMessageShown = viewModel::onRemovalMessageShown,
    )
}

/** Stateless and self-contained so it can be rendered directly in tests with fixture states. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onUserClick: (Int) -> Unit,
    onBrowseUsers: () -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearchQueryChange: (String) -> Unit = {},
    onUndoRemoval: () -> Unit = {},
    onRemovalMessageShown: () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Un-favoriting removes the row immediately, so offer a one-shot undo. Keying on the user id
    // means a rapid second removal restarts this effect: the old snackbar is cancelled and only
    // the latest removal stays undoable, matching what the ViewModel remembers.
    val removedUserId = uiState.removedUserId
    val removedMessage = stringResource(R.string.favorites_removed_message)
    val undoLabel = stringResource(R.string.favorites_undo)
    LaunchedEffect(removedUserId) {
        if (removedUserId != null) {
            val result =
                snackbarHostState.showSnackbar(
                    message = removedMessage,
                    actionLabel = undoLabel,
                    duration = SnackbarDuration.Short,
                )
            when (result) {
                SnackbarResult.ActionPerformed -> onUndoRemoval()
                SnackbarResult.Dismissed -> onRemovalMessageShown()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.favorites_title)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Search only makes sense once there are favorites to filter.
            if (!uiState.isLoading && uiState.favorites.isNotEmpty()) {
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
                    uiState.isLoading ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }

                    uiState.favorites.isEmpty() ->
                        FavoritesEmpty(
                            onBrowseUsers = onBrowseUsers,
                            modifier = Modifier.fillMaxSize(),
                        )

                    uiState.filteredFavorites.isEmpty() ->
                        UserListSearchEmpty(
                            query = uiState.searchQuery,
                            modifier = Modifier.fillMaxSize(),
                        )

                    else ->
                        FavoritesList(
                            favorites = uiState.filteredFavorites,
                            onUserClick = onUserClick,
                            onToggleFavorite = onToggleFavorite,
                        )
                }
            }
        }
    }
}

@Composable
private fun FavoritesList(
    favorites: List<User>,
    onUserClick: (Int) -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        items(favorites, key = { it.id }) { user ->
            UserListItem(
                user = user,
                onClick = { onUserClick(user.id) },
                onToggleFavorite = { onToggleFavorite(user.id, !user.isFavorite) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun FavoritesEmpty(
    onBrowseUsers: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.StarOutline,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.favorites_empty_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.md),
        )
        Text(
            text = stringResource(R.string.favorites_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.xs),
        )
        Button(
            onClick = onBrowseUsers,
            modifier = Modifier.padding(top = Spacing.lg),
        ) {
            Text(stringResource(R.string.favorites_browse_button))
        }
    }
}
