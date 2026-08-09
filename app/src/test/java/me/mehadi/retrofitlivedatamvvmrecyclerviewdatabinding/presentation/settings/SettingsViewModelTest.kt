package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.ClearCacheUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase.RefreshUsersUseCase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.FakeUserPreferencesRepository
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.FakeUserRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferences: FakeUserPreferencesRepository
    private lateinit var userRepository: FakeUserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferences = FakeUserPreferencesRepository()
        userRepository = FakeUserRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() =
        SettingsViewModel(
            userPreferencesRepository = preferences,
            clearCacheUseCase = ClearCacheUseCase(userRepository),
            refreshUsersUseCase = RefreshUsersUseCase(userRepository),
        )

    @Test
    fun `ui state reflects the stored preferences`() =
        runTest {
            preferences.setThemeMode(ThemeMode.DARK)
            preferences.setDynamicColorEnabled(false)

            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(ThemeMode.DARK, state.themeMode)
            assertFalse(state.dynamicColorEnabled)
        }

    @Test
    fun `selecting a theme persists it`() =
        runTest {
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.setThemeMode(ThemeMode.LIGHT)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(ThemeMode.LIGHT, preferences.currentThemeMode)
            assertEquals(ThemeMode.LIGHT, viewModel.uiState.value.themeMode)
        }

    @Test
    fun `toggling dynamic color persists it`() =
        runTest {
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.setDynamicColorEnabled(false)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(preferences.currentDynamicColorEnabled)
            assertFalse(viewModel.uiState.value.dynamicColorEnabled)
        }

    @Test
    fun `clear cache asks for confirmation before doing anything`() =
        runTest {
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onClearCacheClick()

            assertTrue(viewModel.uiState.value.showClearCacheConfirmation)
        }

    @Test
    fun `dismissing the confirmation leaves the cache untouched`() =
        runTest {
            userRepository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))
            userRepository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onClearCacheClick()
            viewModel.onClearCacheDismiss()
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(viewModel.uiState.value.showClearCacheConfirmation)
            assertEquals(1, userRepository.observeUsers().first().size)
        }

    @Test
    fun `confirming clear cache wipes and refetches`() =
        runTest {
            userRepository.usersAfterRefresh = listOf(User(1, "Ada Lovelace", "ada", "ada@example.com"))
            userRepository.refresh()
            val viewModel = createViewModel()
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.onClearCacheClick()
            viewModel.onClearCacheConfirm()
            testDispatcher.scheduler.advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.showClearCacheConfirmation)
            assertFalse(state.isClearingCache)
            // The refetch repopulates the cache after the wipe.
            assertEquals(1, userRepository.observeUsers().first().size)
        }
}
