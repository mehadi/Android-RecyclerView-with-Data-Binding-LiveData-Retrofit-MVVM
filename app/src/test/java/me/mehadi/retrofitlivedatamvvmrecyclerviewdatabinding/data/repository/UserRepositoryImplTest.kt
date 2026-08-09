package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.RefreshError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class UserRepositoryImplTest {
    private lateinit var dao: FakeUserDao
    private lateinit var api: FakeUserApi
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeUserDao()
        api = FakeUserApi()
        repository = UserRepositoryImpl(api, dao)
    }

    @Test
    fun `refresh populates the cache from the network`() =
        runTest {
            api.usersToReturn = listOf(UserDto(1, "Ada Lovelace", "ada", "ada@example.com"))

            val result = repository.refresh()

            assertTrue(result.isSuccess)
            val cached = repository.observeUsers().first()
            assertEquals(1, cached.size)
            assertEquals("Ada Lovelace", cached.first().name)
        }

    @Test
    fun `a failed refresh does not clear an existing cache`() =
        runTest {
            api.usersToReturn = listOf(UserDto(1, "Ada Lovelace", "ada", "ada@example.com"))
            repository.refresh()

            api.errorToThrow = IOException("network down")
            val result = repository.refresh()

            assertTrue(result.isFailure)
            val cached = repository.observeUsers().first()
            assertEquals(1, cached.size)
        }

    @Test
    fun `refresh evicts users no longer present on the server`() =
        runTest {
            api.usersToReturn =
                listOf(
                    UserDto(1, "Ada Lovelace", "ada", "ada@example.com"),
                    UserDto(2, "Alan Turing", "alan", "alan@example.com"),
                )
            repository.refresh()

            api.usersToReturn = listOf(UserDto(1, "Ada Lovelace", "ada", "ada@example.com"))
            repository.refresh()

            val cached = repository.observeUsers().first()
            assertEquals(listOf(1), cached.map { it.id })
        }

    @Test
    fun `a connection failure surfaces as RefreshError_NoConnection`() =
        runTest {
            api.errorToThrow = IOException("network down")

            val result = repository.refresh()

            assertTrue(result.exceptionOrNull() is RefreshError.NoConnection)
        }

    @Test
    fun `a timeout surfaces as RefreshError_Timeout`() =
        runTest {
            api.errorToThrow = SocketTimeoutException("timed out")

            val result = repository.refresh()

            assertTrue(result.exceptionOrNull() is RefreshError.Timeout)
        }

    @Test
    fun `an HTTP error surfaces as RefreshError_Server with its status code`() =
        runTest {
            val body = "".toResponseBody("application/json".toMediaType())
            api.errorToThrow = HttpException(Response.error<Unit>(503, body))

            val result = repository.refresh()

            val error = result.exceptionOrNull() as RefreshError.Server
            assertEquals(503, error.code)
        }

    @Test
    fun `an unexpected failure surfaces as RefreshError_Unknown`() =
        runTest {
            api.errorToThrow = IllegalStateException("malformed payload")

            val result = repository.refresh()

            assertTrue(result.exceptionOrNull() is RefreshError.Unknown)
        }

    @Test
    fun `observeUser returns a single cached user by id`() =
        runTest {
            api.usersToReturn =
                listOf(
                    UserDto(1, "Ada Lovelace", "ada", "ada@example.com"),
                    UserDto(2, "Alan Turing", "alan", "alan@example.com"),
                )
            repository.refresh()

            val user = repository.observeUser(2).first()

            assertEquals("Alan Turing", user?.name)
        }
}
