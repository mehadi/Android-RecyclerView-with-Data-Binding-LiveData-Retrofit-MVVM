package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.Spacing
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.components.UserListItem

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
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.favorites_title)) })
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                uiState.favorites.isEmpty() -> FavoritesEmpty(
                    onBrowseUsers = onBrowseUsers,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> FavoritesList(
                    favorites = uiState.favorites,
                    onUserClick = onUserClick,
                    onToggleFavorite = onToggleFavorite,
                )
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
            imageVector = Icons.Outlined.FavoriteBorder,
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
