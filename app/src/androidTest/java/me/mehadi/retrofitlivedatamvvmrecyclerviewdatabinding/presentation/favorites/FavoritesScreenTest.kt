package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.UsersAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoritesScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setScreen(
        uiState: FavoritesUiState,
        onUserClick: (Int) -> Unit = {},
        onBrowseUsers: () -> Unit = {},
        onToggleFavorite: (Int, Boolean) -> Unit = { _, _ -> },
    ) {
        composeTestRule.setContent {
            UsersAppTheme {
                FavoritesScreen(
                    uiState = uiState,
                    onUserClick = onUserClick,
                    onBrowseUsers = onBrowseUsers,
                    onToggleFavorite = onToggleFavorite,
                )
            }
        }
    }

    @Test
    fun displaysFavoritedUsers() {
        setScreen(
            uiState =
                FavoritesUiState(
                    favorites =
                        listOf(
                            User(1, "Ada Lovelace", "ada", "ada@example.com", isFavorite = true),
                        ),
                    isLoading = false,
                ),
        )

        composeTestRule.onNodeWithText("Ada Lovelace").assertIsDisplayed()
    }

    @Test
    fun displaysEmptyStateWhenNothingIsFavorited() {
        setScreen(uiState = FavoritesUiState(favorites = emptyList(), isLoading = false))

        composeTestRule.onNodeWithText("No favorites yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Browse people").assertIsDisplayed()
    }

    @Test
    fun browseButtonFiresCallback() {
        var browseClicks = 0
        setScreen(
            uiState = FavoritesUiState(favorites = emptyList(), isLoading = false),
            onBrowseUsers = { browseClicks++ },
        )

        composeTestRule.onNodeWithText("Browse people").performClick()

        assertEquals(1, browseClicks)
    }

    @Test
    fun tappingAUserOpensTheirDetails() {
        var clickedId = -1
        setScreen(
            uiState =
                FavoritesUiState(
                    favorites =
                        listOf(
                            User(7, "Grace Hopper", "grace", "grace@example.com", isFavorite = true),
                        ),
                    isLoading = false,
                ),
            onUserClick = { clickedId = it },
        )

        composeTestRule.onNodeWithText("Grace Hopper").performClick()

        assertEquals(7, clickedId)
    }
}
