package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

/** Favorites has no network of its own to fail — it's a live view over the cache, so there's no error state. */
data class FavoritesUiState(
    val favorites: List<User> = emptyList(),
    val isLoading: Boolean = true,
)
