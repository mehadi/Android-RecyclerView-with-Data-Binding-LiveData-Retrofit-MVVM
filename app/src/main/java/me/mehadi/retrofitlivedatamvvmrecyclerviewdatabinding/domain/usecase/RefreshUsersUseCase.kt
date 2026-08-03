package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

/** Triggers a network refresh; the cache (and [ObserveUsersUseCase]'s stream) is updated as a side effect. */
class RefreshUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.refresh()
}
