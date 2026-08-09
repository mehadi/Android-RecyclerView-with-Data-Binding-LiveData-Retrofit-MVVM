package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

/**
 * Single source of truth for user data. Implementations are expected to back
 * [observeUsers] with a local cache so the list stays available offline, and
 * to reconcile that cache with the network only when [refresh] is called.
 */
interface UserRepository {
    fun observeUsers(): Flow<List<User>>

    fun observeUser(id: Int): Flow<User?>

    fun observeFavoriteUsers(): Flow<List<User>>

    suspend fun refresh(): Result<Unit>

    suspend fun toggleFavorite(
        id: Int,
        isFavorite: Boolean,
    )

    suspend fun clearCache()
}
