package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.di

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.AppDatabase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.UserApi
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

/** Verifies the production Hilt modules can build the full object graph. */
@HiltAndroidTest
class DiGraphSmokeTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var userRepository: UserRepository

    @Inject lateinit var secondUserRepository: UserRepository

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject lateinit var userApi: UserApi

    @Inject lateinit var database: AppDatabase

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun productionModulesProvideAllBindings() {
        assertNotNull(userRepository)
        assertNotNull(userPreferencesRepository)
        assertNotNull(userApi)
        assertNotNull(database)
    }

    @Test
    fun repositoriesAreSingletons() {
        assertSame(userRepository, secondUserRepository)
    }
}
