package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

/** Flips the favorite flag for a single user in the cache. */
class ToggleFavoriteUseCase
    @Inject
    constructor(
        private val repository: UserRepository,
    ) {
        suspend operator fun invoke(
            id: Int,
            isFavorite: Boolean,
        ) = repository.toggleFavorite(id, isFavorite)
    }
