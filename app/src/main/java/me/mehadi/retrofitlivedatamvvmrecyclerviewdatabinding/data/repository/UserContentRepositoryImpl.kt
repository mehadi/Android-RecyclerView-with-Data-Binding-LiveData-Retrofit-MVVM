package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserContentDao
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.toDomain
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.UserApi
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.toEntity
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.RefreshError
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserContentRepository
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Same offline-first contract as [UserRepositoryImpl]: Room is the single source of truth and
 * [refreshUserContent] is the only network path. The three endpoints are fetched concurrently
 * and all awaited before anything is written, so a failure on any of them leaves the whole
 * cache untouched rather than half-updated.
 */
@Singleton
class UserContentRepositoryImpl
    @Inject
    constructor(
        private val api: UserApi,
        private val dao: UserContentDao,
    ) : UserContentRepository {
        override fun observePostsForUser(userId: Int): Flow<List<Post>> =
            dao.observePostsForUser(userId).map { entities -> entities.map { it.toDomain() } }

        override fun observeAlbumsForUser(userId: Int): Flow<List<Album>> =
            dao.observeAlbumsForUser(userId).map { entities -> entities.map { it.toDomain() } }

        override fun observeTodosForUser(userId: Int): Flow<List<Todo>> =
            dao.observeTodosForUser(userId).map { entities -> entities.map { it.toDomain() } }

        override suspend fun refreshUserContent(userId: Int): Result<Unit> =
            try {
                coroutineScope {
                    val posts = async { api.getPosts(userId) }
                    val albums = async { api.getAlbums(userId) }
                    val todos = async { api.getTodos(userId) }
                    val postEntities = posts.await().map { it.toEntity() }
                    val albumEntities = albums.await().map { it.toEntity() }
                    val todoEntities = todos.await().map { it.toEntity() }
                    dao.replacePostsForUser(userId, postEntities)
                    dao.replaceAlbumsForUser(userId, albumEntities)
                    dao.replaceTodosForUser(userId, todoEntities)
                }
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
    }
