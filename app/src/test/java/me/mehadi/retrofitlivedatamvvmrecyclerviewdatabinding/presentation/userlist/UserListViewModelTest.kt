package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.RefreshError
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

    private fun createViewModel() =
        UserListViewModel(
            observeUsersUseCase = ObserveUsersUseCase(repository),
            refreshUsersUseCase = RefreshUsersUseCase(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
        )

    @Test
    fun `successful initial load populates users and clears loading`() =
        runTest {
            repository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))

            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(1, state.users.size)
            assertFalse(state.isLoading)
            assertEquals(null, state.errorMessageRes)
        }

    @Test
    fun `a failed initial load surfaces an error with an empty list`() =
        runTest {
            repository.refreshResult =
                Result.failure(RefreshError.NoConnection(IOException("no network")))

            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.users.isEmpty())
            assertFalse(state.isLoading)
            assertEquals(R.string.error_no_connection, state.errorMessageRes)
        }

    @Test
    fun `each refresh error type maps to its own message`() =
        runTest {
            val cause = IOException("boom")
            val cases =
                mapOf(
                    RefreshError.NoConnection(cause) to R.string.error_no_connection,
                    RefreshError.Timeout(cause) to R.string.error_timeout,
                    RefreshError.Server(500, cause) to R.string.error_server,
                    RefreshError.Unknown(cause) to R.string.error_refresh_generic,
                )

            cases.forEach { (error, expectedRes) ->
                repository.refreshResult = Result.failure(error)
                val viewModel = createViewModel()
                testDispatcher.scheduler.advanceUntilIdle()

                assertEquals(expectedRes, viewModel.uiState.value.errorMessageRes)
            }
        }

    @Test
    fun `refresh completes without leaving loading flags set`() =
        runTest {
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
    fun `dismissError clears the error message`() =
        runTest {
            repository.refreshResult =
                Result.failure(RefreshError.NoConnection(IOException("no network")))
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.dismissError()

            assertEquals(null, viewModel.uiState.value.errorMessageRes)
        }

    @Test
    fun `sort order re-sorts the filtered list by the chosen field`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(
                    User(1, "Bob Smith", "azzz", "bob@example.com", company = "Zeta Co"),
                    User(2, "Ada Lovelace", "mzzz", "ada@example.com", company = "Alpha Co"),
                )
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                listOf(2, 1),
                viewModel.uiState.value.filteredUsers
                    .map { it.id },
            )

            viewModel.onSortOrderChange(UserSortOrder.USERNAME)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                listOf(1, 2),
                viewModel.uiState.value.filteredUsers
                    .map { it.id },
            )

            viewModel.onSortOrderChange(UserSortOrder.COMPANY)
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                listOf(2, 1),
                viewModel.uiState.value.filteredUsers
                    .map { it.id },
            )
        }

    @Test
    fun `favoritesOnly hides non-favorited users`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(
                    User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true),
                    User(2, "Alan Turing", "alan", "alan@example.com"),
                )
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onFavoritesOnlyChange(true)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                listOf(1),
                viewModel.uiState.value.filteredUsers
                    .map { it.id },
            )
            assertTrue(viewModel.uiState.value.favoritesOnly)
        }

    @Test
    fun `search, favoritesOnly, and sort compose together`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(
                    User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true),
                    User(2, "Ada Turing", "aturing", "aturing@example.com", isFavorite = true),
                    User(3, "Ada Byron", "abyron", "abyron@example.com"),
                )
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onSearchQueryChange("ada")
            viewModel.onFavoritesOnlyChange(true)
            viewModel.onSortOrderChange(UserSortOrder.USERNAME)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                listOf(1, 2),
                viewModel.uiState.value.filteredUsers
                    .map { it.id },
            )
        }
}
