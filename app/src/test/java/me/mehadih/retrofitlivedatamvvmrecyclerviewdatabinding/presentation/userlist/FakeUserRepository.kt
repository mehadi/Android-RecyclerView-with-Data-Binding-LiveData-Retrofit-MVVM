package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository

class FakeUserRepository : UserRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())

    /** What [refresh] should return; on success, [usersAfterRefresh] is published to observers. */
    var refreshResult: Result<Unit> = Result.success(Unit)
    var usersAfterRefresh: List<User> = emptyList()

    override fun observeUsers(): Flow<List<User>> = usersFlow

    override fun observeUser(id: Int): Flow<User?> =
        usersFlow.map { users -> users.find { it.id == id } }

    override fun observeFavoriteUsers(): Flow<List<User>> =
        usersFlow.map { users -> users.filter { it.isFavorite } }

    override suspend fun refresh(): Result<Unit> {
        if (refreshResult.isSuccess) {
            usersFlow.value = usersAfterRefresh
        }
        return refreshResult
    }

    override suspend fun toggleFavorite(id: Int, isFavorite: Boolean) {
        usersFlow.value = usersFlow.value.map { if (it.id == id) it.copy(isFavorite = isFavorite) else it }
    }

    override suspend fun clearCache() {
        usersFlow.value = emptyList()
    }
}
