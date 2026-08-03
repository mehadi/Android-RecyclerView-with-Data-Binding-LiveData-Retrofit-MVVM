package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserDao
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserEntity

/** In-memory stand-in for Room; [UserDao.replaceAll]'s default transaction body runs unchanged. */
class FakeUserDao : UserDao {

    private val usersFlow = MutableStateFlow<List<UserEntity>>(emptyList())

    override fun observeUsers(): Flow<List<UserEntity>> = usersFlow

    override fun observeUserById(id: Int): Flow<UserEntity?> =
        usersFlow.map { users -> users.find { it.id == id } }

    override fun observeFavoriteUsers(): Flow<List<UserEntity>> =
        usersFlow.map { users -> users.filter { it.isFavorite } }

    override suspend fun getFavoriteIds(): List<Int> =
        usersFlow.value.filter { it.isFavorite }.map { it.id }

    override suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        usersFlow.value = usersFlow.value.map { if (it.id == id) it.copy(isFavorite = isFavorite) else it }
    }

    override suspend fun upsertAll(users: List<UserEntity>) {
        val merged = usersFlow.value.associateBy { it.id }.toMutableMap()
        users.forEach { merged[it.id] = it }
        usersFlow.value = merged.values.sortedBy { it.name }
    }

    override suspend fun deleteMissing(keepIds: List<Int>) {
        usersFlow.value = usersFlow.value.filter { it.id in keepIds }
    }

    override suspend fun clearAll() {
        usersFlow.value = emptyList()
    }
}
