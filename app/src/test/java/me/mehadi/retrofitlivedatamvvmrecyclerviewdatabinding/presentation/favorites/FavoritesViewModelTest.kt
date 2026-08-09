package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ObserveFavoriteUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ToggleFavoriteUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.FakeUserRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
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
        FavoritesViewModel(
            observeFavoriteUsersUseCase = ObserveFavoriteUsersUseCase(repository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
        )

    @Test
    fun `only favorited users appear in the list`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(
                    User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true),
                    User(2, "Alan Turing", "alan", "alan@example.com"),
                )
            repository.refresh()

            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(listOf(1), state.favorites.map { it.id })
            assertFalse(state.isLoading)
        }

    @Test
    fun `unfavoriting a user drops them from the list`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true))
            repository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleFavorite(1, false)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(
                viewModel.uiState.value.favorites
                    .isEmpty(),
            )
        }

    @Test
    fun `unfavoriting records the removed user for undo`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true))
            repository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleFavorite(1, false)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, viewModel.uiState.value.removedUserId)
        }

    @Test
    fun `undoRemoval restores the favorite and clears the pending removal`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true))
            repository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleFavorite(1, false)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.undoRemoval()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(null, viewModel.uiState.value.removedUserId)
            assertEquals(
                listOf(1),
                viewModel.uiState.value.favorites
                    .map { it.id },
            )
        }

    @Test
    fun `onRemovalMessageShown clears the pending removal without restoring it`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true))
            repository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.toggleFavorite(1, false)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onRemovalMessageShown()

            assertEquals(null, viewModel.uiState.value.removedUserId)
            assertTrue(
                viewModel.uiState.value.favorites
                    .isEmpty(),
            )
        }

    @Test
    fun `search filters favorites by name, username, or email`() =
        runTest {
            repository.usersAfterRefresh =
                listOf(
                    User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true),
                    User(2, "Alan Turing", "alan", "alan@example.com", isFavorite = true),
                )
            repository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onSearchQueryChange("lovelace")
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                listOf(1),
                viewModel.uiState.value.filteredFavorites
                    .map { it.id },
            )
            assertEquals("lovelace", viewModel.uiState.value.searchQuery)
        }
}
