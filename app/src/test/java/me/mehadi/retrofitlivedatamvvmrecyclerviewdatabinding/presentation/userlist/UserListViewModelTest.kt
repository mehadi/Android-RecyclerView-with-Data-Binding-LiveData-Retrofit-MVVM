package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeUserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = UserListViewModel(
        observeUsersUseCase = ObserveUsersUseCase(repository),
        refreshUsersUseCase = RefreshUsersUseCase(repository),
        toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
    )

    @Test
    fun `successful initial load populates users and clears loading`() = runTest {
        repository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.users.size)
        assertFalse(state.isLoading)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `a failed initial load surfaces an error with an empty list`() = runTest {
        repository.refreshResult = Result.failure(IOException("no network"))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.users.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("no network", state.errorMessage)
    }

    @Test
    fun `refresh completes without leaving loading flags set`() = runTest {
        repository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertEquals(1, state.users.size)
    }

    @Test
    fun `dismissError clears the error message`() = runTest {
        repository.refreshResult = Result.failure(IOException("no network"))
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dismissError()

        assertEquals(null, viewModel.uiState.value.errorMessage)
    }
}
