package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.UsersAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(onFinished: () -> Unit = {}) {
        composeTestRule.setContent {
            UsersAppTheme {
                OnboardingScreen(onFinished = onFinished)
            }
        }
    }

    @Test
    fun firstPageShowsSkipAndNext() {
        setScreen()

        composeTestRule.onNodeWithText("Browse a community of people").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun skipFinishesOnboarding() {
        var finished = 0
        setScreen(onFinished = { finished++ })

        composeTestRule.onNodeWithText("Skip").performClick()

        assertEquals(1, finished)
    }

    @Test
    fun nextAdvancesToTheSecondPage() {
        setScreen()

        composeTestRule.onNodeWithText("Next").performClick()

        composeTestRule
            .onNodeWithText("Search and favorite the ones you care about")
            .assertIsDisplayed()
    }

    @Test
    fun lastPageShowsGetStartedWhichFinishes() {
        var finished = 0
        setScreen(onFinished = { finished++ })

        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("Get Started").performClick()

        assertEquals(1, finished)
    }
}
