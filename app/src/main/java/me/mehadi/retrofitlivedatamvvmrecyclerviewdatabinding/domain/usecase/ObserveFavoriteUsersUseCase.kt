package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

/** Observes only the favorited users from the cache; works offline since it never touches the network. */
class ObserveFavoriteUsersUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<List<User>> = repository.observeFavoriteUsers()
}
