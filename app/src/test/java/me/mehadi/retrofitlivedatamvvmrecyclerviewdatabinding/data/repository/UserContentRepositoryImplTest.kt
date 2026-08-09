package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.AlbumDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.PostDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.TodoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UserContentRepositoryImplTest {
    private lateinit var api: FakeUserApi
    private lateinit var dao: FakeUserContentDao
    private lateinit var repository: UserContentRepositoryImpl

    @Before
    fun setUp() {
        api = FakeUserApi()
        dao = FakeUserContentDao()
        repository = UserContentRepositoryImpl(api, dao)
    }

    @Test
    fun `refreshUserContent populates posts, albums, and todos`() =
        runTest {
            api.postsToReturn = listOf(PostDto(id = 1, userId = 1, title = "Post", body = "Body"))
            api.albumsToReturn = listOf(AlbumDto(id = 1, userId = 1, title = "Album"))
            api.todosToReturn = listOf(TodoDto(id = 1, userId = 1, title = "Todo", completed = true))

            val result = repository.refreshUserContent(1)

            assertTrue(result.isSuccess)
            assertEquals(listOf("Post"), repository.observePostsForUser(1).first().map { it.title })
            assertEquals(listOf("Album"), repository.observeAlbumsForUser(1).first().map { it.title })
            assertEquals(listOf(true), repository.observeTodosForUser(1).first().map { it.isCompleted })
        }

    @Test
    fun `a failure on any endpoint leaves all cached content untouched`() =
        runTest {
            api.postsToReturn = listOf(PostDto(id = 1, userId = 1, title = "Cached post", body = "Body"))
            api.albumsToReturn = listOf(AlbumDto(id = 1, userId = 1, title = "Cached album"))
            api.todosToReturn = listOf(TodoDto(id = 1, userId = 1, title = "Cached todo", completed = false))
            repository.refreshUserContent(1)

            api.postsToReturn = listOf(PostDto(id = 2, userId = 1, title = "New post", body = "Body"))
            api.albumsErrorToThrow = IOException("albums down")

            val result = repository.refreshUserContent(1)

            assertTrue(result.isFailure)
            assertEquals(listOf("Cached post"), repository.observePostsForUser(1).first().map { it.title })
            assertEquals(listOf("Cached album"), repository.observeAlbumsForUser(1).first().map { it.title })
            assertEquals(listOf("Cached todo"), repository.observeTodosForUser(1).first().map { it.title })
        }

    @Test
    fun `observe flows are scoped to the requested user`() =
        runTest {
            api.postsToReturn =
                listOf(
                    PostDto(id = 1, userId = 1, title = "User 1 post", body = "Body"),
                    PostDto(id = 2, userId = 2, title = "User 2 post", body = "Body"),
                )
            repository.refreshUserContent(1)
            repository.refreshUserContent(2)

            assertEquals(listOf("User 1 post"), repository.observePostsForUser(1).first().map { it.title })
            assertEquals(listOf("User 2 post"), repository.observePostsForUser(2).first().map { it.title })
        }

    @Test
    fun `refreshing again evicts stale rows for that user only, leaving other users intact`() =
        runTest {
            api.postsToReturn =
                listOf(
                    PostDto(id = 1, userId = 1, title = "Stale", body = "Body"),
                    PostDto(id = 2, userId = 2, title = "Other user's post", body = "Body"),
                )
            repository.refreshUserContent(1)
            repository.refreshUserContent(2)

            api.postsToReturn = listOf(PostDto(id = 3, userId = 1, title = "Fresh", body = "Body"))
            repository.refreshUserContent(1)

            assertEquals(listOf("Fresh"), repository.observePostsForUser(1).first().map { it.title })
            assertEquals(listOf("Other user's post"), repository.observePostsForUser(2).first().map { it.title })
        }
}
