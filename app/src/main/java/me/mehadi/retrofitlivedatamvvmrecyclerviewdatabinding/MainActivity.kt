package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.AppNavHost
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.Destinations
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.MainActivityUiState
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.navigation.MainActivityViewModel
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.theme.UsersAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Mirrors [MainActivityViewModel.uiState] so the splash screen's keep-on-screen condition
     * below (which is polled outside of composition, on every pre-draw pass) can read it. A
     * Compose [mutableStateOf] is used rather than a plain field purely so writes from inside
     * [setContent]'s composition are visible to that callback.
     */
    private var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the splash screen up until the initial theme + onboarding-seen preferences have
        // loaded, so we never flash the wrong theme or the wrong start destination.
        splashScreen.setKeepOnScreenCondition { uiState is MainActivityUiState.Loading }

        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            val currentState by viewModel.uiState.collectAsStateWithLifecycle()
            uiState = currentState

            val readyState = currentState as? MainActivityUiState.Ready
            if (readyState != null) {
                UsersAppTheme(
                    darkTheme =
                        when (readyState.themeMode) {
                            ThemeMode.SYSTEM -> isSystemInDarkTheme()
                            ThemeMode.LIGHT -> false
                            ThemeMode.DARK -> true
                        },
                    dynamicColor = readyState.dynamicColorEnabled,
                ) {
                    AppNavHost(
                        startDestination =
                            if (readyState.onboardingSeen) {
                                Destinations.USER_LIST
                            } else {
                                Destinations.ONBOARDING
                            },
                    )
                }
            }
        }
    }
}
