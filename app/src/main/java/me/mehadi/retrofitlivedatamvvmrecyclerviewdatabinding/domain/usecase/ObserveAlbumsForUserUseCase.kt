package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserContentRepository
import javax.inject.Inject

/** Observes one user's cached albums; works offline since it never touches the network. */
class ObserveAlbumsForUserUseCase
    @Inject
    constructor(
        private val repository: UserContentRepository,
    ) {
        operator fun invoke(userId: Int): Flow<List<Album>> = repository.observeAlbumsForUser(userId)
    }
