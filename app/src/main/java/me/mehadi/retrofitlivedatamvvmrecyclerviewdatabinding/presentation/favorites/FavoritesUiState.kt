package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

/** Favorites has no network of its own to fail — it's a live view over the cache, so there's no error state. */
data class FavoritesUiState(
    val favorites: List<User> = emptyList(),
    val searchQuery: String = "",
    // Defaults to the unfiltered list so fixture states that only set [favorites] (e.g. tests,
    // previews) render correctly without also having to derive this themselves.
    val filteredFavorites: List<User> = favorites,
    val isLoading: Boolean = true,
    /** Last user un-favorited from this screen, kept until the undo snackbar is resolved. */
    val removedUserId: Int? = null,
)
