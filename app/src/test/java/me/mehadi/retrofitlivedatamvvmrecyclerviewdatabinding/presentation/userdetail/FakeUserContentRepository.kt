package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserContentRepository

class FakeUserContentRepository : UserContentRepository {
    private val postsFlow = MutableStateFlow<List<Post>>(emptyList())
    private val albumsFlow = MutableStateFlow<List<Album>>(emptyList())
    private val todosFlow = MutableStateFlow<List<Todo>>(emptyList())

    /** What [refreshUserContent] should return; on success the *AfterRefresh lists are published. */
    var refreshResult: Result<Unit> = Result.success(Unit)
    var postsAfterRefresh: List<Post> = emptyList()
    var albumsAfterRefresh: List<Album> = emptyList()
    var todosAfterRefresh: List<Todo> = emptyList()

    override fun observePostsForUser(userId: Int): Flow<List<Post>> = postsFlow.map { posts -> posts.filter { it.userId == userId } }

    override fun observeAlbumsForUser(userId: Int): Flow<List<Album>> = albumsFlow.map { albums -> albums.filter { it.userId == userId } }

    override fun observeTodosForUser(userId: Int): Flow<List<Todo>> = todosFlow.map { todos -> todos.filter { it.userId == userId } }

    override suspend fun refreshUserContent(userId: Int): Result<Unit> {
        if (refreshResult.isSuccess) {
            postsFlow.value = postsAfterRefresh
            albumsFlow.value = albumsAfterRefresh
            todosFlow.value = todosAfterRefresh
        }
        return refreshResult
    }
}
