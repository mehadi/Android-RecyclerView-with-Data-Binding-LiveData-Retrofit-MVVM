package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.datastore.UserPreferencesDataSource
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserPreferencesRepositoryImplTest {
    private val dataSource = UserPreferencesDataSource(ApplicationProvider.getApplicationContext())
    private val repository = UserPreferencesRepositoryImpl(dataSource)

    // The process-wide DataStore file is shared across test methods, so the scenario runs as one
    // sequential test: defaults are only observable before anything has been written.
    @Test
    fun defaultsRoundTripAndBadDataFallback() =
        runBlocking {
            assertEquals(ThemeMode.SYSTEM, repository.themeMode.first())
            assertTrue(repository.dynamicColorEnabled.first())
            assertFalse(repository.onboardingSeen.first())

            repository.setThemeMode(ThemeMode.DARK)
            repository.setDynamicColorEnabled(false)
            repository.setOnboardingSeen(true)

            assertEquals(ThemeMode.DARK, repository.themeMode.first())
            assertFalse(repository.dynamicColorEnabled.first())
            assertTrue(repository.onboardingSeen.first())

            // A corrupt/unknown stored value must degrade to the default, not crash.
            dataSource.setThemeMode("NOT_A_THEME")
            assertEquals(ThemeMode.SYSTEM, repository.themeMode.first())
        }
}
