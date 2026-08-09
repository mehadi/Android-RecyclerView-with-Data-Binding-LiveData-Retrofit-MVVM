package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.AlbumEntity
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.PostEntity
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.TodoEntity
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserContentDao

/** In-memory stand-in for Room; the default `replace*ForUser` transaction bodies run unchanged. */
class FakeUserContentDao : UserContentDao {
    private val postsFlow = MutableStateFlow<List<PostEntity>>(emptyList())
    private val albumsFlow = MutableStateFlow<List<AlbumEntity>>(emptyList())
    private val todosFlow = MutableStateFlow<List<TodoEntity>>(emptyList())

    override fun observePostsForUser(userId: Int): Flow<List<PostEntity>> = postsFlow.map { posts -> posts.filter { it.userId == userId } }

    override fun observeAlbumsForUser(userId: Int): Flow<List<AlbumEntity>> =
        albumsFlow.map { albums -> albums.filter { it.userId == userId } }

    override fun observeTodosForUser(userId: Int): Flow<List<TodoEntity>> = todosFlow.map { todos -> todos.filter { it.userId == userId } }

    override suspend fun deletePostsForUser(userId: Int) {
        postsFlow.value = postsFlow.value.filter { it.userId != userId }
    }

    override suspend fun deleteAlbumsForUser(userId: Int) {
        albumsFlow.value = albumsFlow.value.filter { it.userId != userId }
    }

    override suspend fun deleteTodosForUser(userId: Int) {
        todosFlow.value = todosFlow.value.filter { it.userId != userId }
    }

    override suspend fun insertPosts(posts: List<PostEntity>) {
        postsFlow.value = postsFlow.value + posts
    }

    override suspend fun insertAlbums(albums: List<AlbumEntity>) {
        albumsFlow.value = albumsFlow.value + albums
    }

    override suspend fun insertTodos(todos: List<TodoEntity>) {
        todosFlow.value = todosFlow.value + todos
    }
}
