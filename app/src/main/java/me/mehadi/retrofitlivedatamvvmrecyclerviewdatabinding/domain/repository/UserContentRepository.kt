package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo

/**
 * Per-user content (posts, albums, todos) following the same offline-first contract as
 * [UserRepository]: the observe methods read only from the local cache, and
 * [refreshUserContent] is the only place that talks to the network.
 */
interface UserContentRepository {
    fun observePostsForUser(userId: Int): Flow<List<Post>>

    fun observeAlbumsForUser(userId: Int): Flow<List<Album>>

    fun observeTodosForUser(userId: Int): Flow<List<Todo>>

    suspend fun refreshUserContent(userId: Int): Result<Unit>
}
