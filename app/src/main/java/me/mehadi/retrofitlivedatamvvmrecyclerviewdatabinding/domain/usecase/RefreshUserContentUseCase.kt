package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserContentRepository
import javax.inject.Inject

/** Triggers a network refresh of one user's posts, albums, and todos; the cached streams update as a side effect. */
class RefreshUserContentUseCase
    @Inject
    constructor(
        private val repository: UserContentRepository,
    ) {
        suspend operator fun invoke(userId: Int): Result<Unit> = repository.refreshUserContent(userId)
    }
