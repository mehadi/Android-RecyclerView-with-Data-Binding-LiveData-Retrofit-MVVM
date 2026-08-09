package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserContentRepository
import javax.inject.Inject

/** Observes one user's cached posts; works offline since it never touches the network. */
class ObservePostsForUserUseCase
    @Inject
    constructor(
        private val repository: UserContentRepository,
    ) {
        operator fun invoke(userId: Int): Flow<List<Post>> = repository.observePostsForUser(userId)
    }
