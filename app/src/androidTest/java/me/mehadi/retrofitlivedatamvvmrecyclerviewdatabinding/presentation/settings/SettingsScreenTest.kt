package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.UsersAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        uiState: SettingsUiState,
        onThemeModeSelected: (ThemeMode) -> Unit = {},
        onClearCacheClick: () -> Unit = {},
        onClearCacheConfirm: () -> Unit = {},
        onClearCacheDismiss: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            UsersAppTheme {
                SettingsScreen(
                    uiState = uiState,
                    onThemeModeSelected = onThemeModeSelected,
                    onDynamicColorToggle = {},
                    onClearCacheClick = onClearCacheClick,
                    onClearCacheConfirm = onClearCacheConfirm,
                    onClearCacheDismiss = onClearCacheDismiss,
                )
            }
        }
    }

    @Test
    fun displaysSectionsAndVersion() {
        setScreen(uiState = SettingsUiState(appVersion = "9.9-test"))

        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear cache").assertIsDisplayed()
        composeTestRule.onNodeWithText("9.9-test").assertIsDisplayed()
    }

    @Test
    fun selectingAThemeFiresCallback() {
        var selected: ThemeMode? = null
        setScreen(
            uiState = SettingsUiState(),
            onThemeModeSelected = { selected = it },
        )

        composeTestRule.onNodeWithText("Dark").performClick()

        assertEquals(ThemeMode.DARK, selected)
    }

    @Test
    fun clearCacheRowAsksForConfirmation() {
        var clicks = 0
        setScreen(
            uiState = SettingsUiState(),
            onClearCacheClick = { clicks++ },
        )

        composeTestRule.onNodeWithText("Clear cache").performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun confirmationDialogConfirmAndCancelFireCallbacks() {
        var confirmed = 0
        setScreen(
            uiState = SettingsUiState(showClearCacheConfirmation = true),
            onClearCacheConfirm = { confirmed++ },
        )

        composeTestRule.onNodeWithText("Clear cache?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").performClick()

        assertEquals(1, confirmed)
    }
}
