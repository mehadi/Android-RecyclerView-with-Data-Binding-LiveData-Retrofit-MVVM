package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.RefreshError
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveAlbumsForUserUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObservePostsForUserUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveTodosForUserUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveUserByIdUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUserContentUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.NavArgs
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.FakeUserRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeUserRepository
    private lateinit var contentRepository: FakeUserContentRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeUserRepository()
        contentRepository = FakeUserContentRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(userId: Int) =
        UserDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(NavArgs.USER_ID to userId)),
            observeUserByIdUseCase = ObserveUserByIdUseCase(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
            observePostsForUserUseCase = ObservePostsForUserUseCase(contentRepository),
            observeTodosForUserUseCase = ObserveTodosForUserUseCase(contentRepository),
            observeAlbumsForUserUseCase = ObserveAlbumsForUserUseCase(contentRepository),
            refreshUserContentUseCase = RefreshUserContentUseCase(contentRepository),
        )

    @Test
    fun `loads the user matching the navigation argument`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(
                    User(1, "Ada Lovelace", "ada", "ada@example.com"),
                    User(2, "Alan Turing", "alan", "alan@example.com"),
                )
            repository.refresh()

            val viewModel = createViewModel(userId = 2)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals("Alan Turing", state.user?.name)
            assertFalse(state.isLoading)
        }

    @Test
    fun `an unknown id resolves to no user without staying in loading`() =
        runTest {
            val viewModel = createViewModel(userId = 99)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNull(state.user)
            assertFalse(state.isLoading)
        }

    @Test
    fun `toggling favorite flips the current user's flag`() =
        runTest {
            repository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))
            repository.refresh()
            val viewModel = createViewModel(userId = 1)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onToggleFavorite()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value.user
                    ?.isFavorite == true,
            )
            assertTrue(repository.observeUser(1).first()?.isFavorite == true)
        }

    @Test
    fun `content refresh publishes the user's posts`() =
        runTest {
            repository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))
            repository.refresh()
            contentRepository.postsAfterRefresh =
                listOf(Post(id = 10, userId = 1, title = "Hello", body = "World"))

            val viewModel = createViewModel(userId = 1)
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(listOf(10), state.posts.map { it.id })
            assertFalse(state.isContentLoading)
            assertNull(state.contentErrorRes)
        }

    @Test
    fun `a failed content refresh surfaces an error and retry recovers`() =
        runTest {
            repository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))
            repository.refresh()
            contentRepository.refreshResult =
                Result.failure(RefreshError.NoConnection(java.io.IOException("no network")))
            contentRepository.postsAfterRefresh =
                listOf(Post(id = 10, userId = 1, title = "Hello", body = "World"))

            val viewModel = createViewModel(userId = 1)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(R.string.error_no_connection, viewModel.uiState.value.contentErrorRes)

            contentRepository.refreshResult = Result.success(Unit)
            viewModel.retryContent()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertNull(state.contentErrorRes)
            assertEquals(listOf(10), state.posts.map { it.id })
        }
}
