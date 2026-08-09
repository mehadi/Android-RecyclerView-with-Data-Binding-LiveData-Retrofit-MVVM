package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.R
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.favorites.FavoritesRoute
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.onboarding.OnboardingRoute
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.settings.SettingsRoute
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail.UserDetailRoute
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userlist.UserListRoute

object NavArgs {
    const val USER_ID = "userId"
}

/** Route constants for [AppNavHost]. Public so [me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.MainActivity] can pick a start destination. */
object Destinations {
    const val ONBOARDING = "onboarding"
    const val USER_LIST = "userList"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val USER_DETAIL = "userDetail/{${NavArgs.USER_ID}}"

    fun userDetail(userId: Int) = "userDetail/$userId"
}

/** The three top-level, always-reachable destinations shown in the bottom [NavigationBar]. */
private data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

private val bottomNavItems =
    listOf(
        BottomNavItem(
            route = Destinations.USER_LIST,
            labelRes = R.string.bottom_nav_home,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
        ),
        BottomNavItem(
            route = Destinations.FAVORITES,
            labelRes = R.string.bottom_nav_favorites,
            // Star (not heart) to match the favorite toggle icon used on list rows.
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.StarOutline,
        ),
        BottomNavItem(
            route = Destinations.SETTINGS,
            labelRes = R.string.bottom_nav_settings,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
        ),
    )

/** Destinations that own their own full-screen presentation and hide the bottom nav bar. */
private val destinationsWithoutBottomBar = setOf(Destinations.ONBOARDING, Destinations.USER_DETAIL)

@Composable
fun AppNavHost(startDestination: String = Destinations.USER_LIST) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in destinationsWithoutBottomBar

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = { route ->
                        navController.navigate(route) {
                            // Top-level tabs behave like separate stacks: reselecting a tab
                            // restores its last state instead of piling up duplicate entries.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            // The padding above already reserves space for the bottom bar/system insets; consume
            // it so nested per-screen Scaffolds don't double-apply the same insets themselves.
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .fillMaxSize(),
        ) {
            composable(Destinations.ONBOARDING) {
                OnboardingRoute(
                    onFinished = {
                        navController.navigate(Destinations.USER_LIST) {
                            popUpTo(Destinations.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }

            composable(Destinations.USER_LIST) {
                UserListRoute(
                    onUserClick = { userId -> navController.navigate(Destinations.userDetail(userId)) },
                )
            }

            composable(Destinations.FAVORITES) {
                FavoritesRoute(
                    onUserClick = { userId -> navController.navigate(Destinations.userDetail(userId)) },
                    onBrowseUsers = {
                        navController.navigate(Destinations.USER_LIST) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }

            composable(Destinations.SETTINGS) {
                SettingsRoute()
            }

            composable(
                route = Destinations.USER_DETAIL,
                arguments = listOf(navArgument(NavArgs.USER_ID) { type = NavType.IntType }),
            ) {
                UserDetailRoute(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun AppBottomNavigationBar(
    currentRoute: String?,
    onItemSelected: (String) -> Unit,
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(item.labelRes)) },
            )
        }
    }
}
