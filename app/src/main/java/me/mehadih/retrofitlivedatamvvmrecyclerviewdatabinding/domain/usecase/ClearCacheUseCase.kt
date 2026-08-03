package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

/** Wipes the local user cache; the next [ObserveUsersUseCase] emission is an empty list until the next refresh. */
class ClearCacheUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke() = repository.clearCache()
}
