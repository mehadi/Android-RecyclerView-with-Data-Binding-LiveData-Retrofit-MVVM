package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.onboarding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.FakeUserPreferencesRepository
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferences: FakeUserPreferencesRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        preferences = FakeUserPreferencesRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `finishing onboarding persists the seen flag`() =
        runTest {
            val viewModel = OnboardingViewModel(preferences)
            assertFalse(preferences.currentOnboardingSeen)

            viewModel.onOnboardingFinished()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(preferences.currentOnboardingSeen)
        }
}
