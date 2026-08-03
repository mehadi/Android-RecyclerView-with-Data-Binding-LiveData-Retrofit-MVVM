package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.datastore.UserPreferencesDataSource
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.ThemeMode
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Maps the raw DataStore-backed [UserPreferencesDataSource] onto typed domain defaults. */
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {

    override val themeMode: Flow<ThemeMode> = dataSource.themeMode.map { stored ->
        stored?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM
    }

    override val dynamicColorEnabled: Flow<Boolean> =
        dataSource.dynamicColorEnabled.map { it ?: true }

    override val onboardingSeen: Flow<Boolean> =
        dataSource.onboardingSeen.map { it ?: false }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataSource.setThemeMode(themeMode.name)
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean) {
        dataSource.setDynamicColorEnabled(enabled)
    }

    override suspend fun setOnboardingSeen(seen: Boolean) {
        dataSource.setOnboardingSeen(seen)
    }
}
