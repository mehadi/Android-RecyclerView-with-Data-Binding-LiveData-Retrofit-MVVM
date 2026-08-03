package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

/** Observes the cached user list; works offline since it never touches the network. */
class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<List<User>> = repository.observeUsers()
}
