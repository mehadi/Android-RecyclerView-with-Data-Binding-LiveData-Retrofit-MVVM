package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserDao
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.toDomain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.UserApi
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.toEntity
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.RefreshError
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Room is the single source of truth: [observeUsers] always reads from the local cache (so the UI
 * works offline), while [refresh] is the only place that talks to the network and writes through
 * to Room. A failed refresh surfaces as [Result.failure] but never clears the existing cache.
 */
@Singleton
class UserRepositoryImpl
    @Inject
    constructor(
        private val api: UserApi,
        private val dao: UserDao,
    ) : UserRepository {
        override fun observeUsers(): Flow<List<User>> = dao.observeUsers().map { entities -> entities.map { it.toDomain() } }

        override fun observeUser(id: Int): Flow<User?> = dao.observeUserById(id).map { entity -> entity?.toDomain() }

        override fun observeFavoriteUsers(): Flow<List<User>> =
            dao.observeFavoriteUsers().map { entities -> entities.map { it.toDomain() } }

        override suspend fun refresh(): Result<Unit> =
            try {
                // The network DTO has no notion of favorites, so a naive upsert would reset everyone's
                // isFavorite to false on every pull-to-refresh. Preserve it by re-applying the current
                // favorite ids onto the freshly-mapped entities before they replace the cache.
                val favoriteIds = dao.getFavoriteIds().toSet()
                val users =
                    api.getUsers().map { dto ->
                        val entity = dto.toEntity()
                        if (entity.id in favoriteIds) entity.copy(isFavorite = true) else entity
                    }
                dao.replaceAll(users)
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: SocketTimeoutException) {
                Result.failure(RefreshError.Timeout(e))
            } catch (e: HttpException) {
                Result.failure(RefreshError.Server(e.code(), e))
            } catch (e: IOException) {
                Result.failure(RefreshError.NoConnection(e))
            } catch (e: Exception) {
                Result.failure(RefreshError.Unknown(e))
            }

        override suspend fun toggleFavorite(
            id: Int,
            isFavorite: Boolean,
        ) {
            dao.setFavorite(id, isFavorite)
        }

        override suspend fun clearCache() {
            dao.clearAll()
        }
    }
