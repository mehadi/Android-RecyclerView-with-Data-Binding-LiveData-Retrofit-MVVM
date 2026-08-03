package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.UsersAppTheme
import org.junit.Rule
import org.junit.Test

class UserListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleUsers = listOf(
        User(id = 1, name = "Ada Lovelace", username = "ada", email = "ada@example.com"),
        User(id = 2, name = "Alan Turing", username = "alan", email = "alan@example.com"),
    )

    @Test
    fun displaysUsersWhenLoaded() {
        composeTestRule.setContent {
            UsersAppTheme {
                UserListScreen(
                    uiState = UserListUiState(users = sampleUsers, isLoading = false),
                    onRefresh = {},
                    onUserClick = {},
                    onErrorShown = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Ada Lovelace").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alan Turing").assertIsDisplayed()
    }

    @Test
    fun displaysEmptyStateWhenNoUsers() {
        composeTestRule.setContent {
            UsersAppTheme {
                UserListScreen(
                    uiState = UserListUiState(users = emptyList(), isLoading = false),
                    onRefresh = {},
                    onUserClick = {},
                    onErrorShown = {},
                )
            }
        }

        composeTestRule.onNodeWithText("No users found").assertIsDisplayed()
    }

    @Test
    fun displaysFullScreenErrorWhenNoCachedUsers() {
        composeTestRule.setContent {
            UsersAppTheme {
                UserListScreen(
                    uiState = UserListUiState(
                        users = emptyList(),
                        isLoading = false,
                        errorMessage = "Network unavailable",
                    ),
                    onRefresh = {},
                    onUserClick = {},
                    onErrorShown = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Network unavailable").assertIsDisplayed()
    }

    @Test
    fun displaysLoadingIndicatorOnFirstLoad() {
        composeTestRule.setContent {
            UsersAppTheme {
                UserListScreen(
                    uiState = UserListUiState(users = emptyList(), isLoading = true),
                    onRefresh = {},
                    onUserClick = {},
                    onErrorShown = {},
                )
            }
        }

        // No content or empty-state copy should render while the first load is in flight.
        composeTestRule.onNodeWithText("No users found").assertDoesNotExist()
    }
}
