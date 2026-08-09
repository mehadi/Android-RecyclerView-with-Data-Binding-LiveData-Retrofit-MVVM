package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserContentRepository
import javax.inject.Inject

/** Observes one user's cached todos; works offline since it never touches the network. */
class ObserveTodosForUserUseCase
    @Inject
    constructor(
        private val repository: UserContentRepository,
    ) {
        operator fun invoke(userId: Int): Flow<List<Todo>> = repository.observeTodosForUser(userId)
    }
