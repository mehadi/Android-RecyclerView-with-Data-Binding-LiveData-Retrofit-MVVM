package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

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
    fun `refresh populates the cache from the network`() = runTest {
        api.usersToReturn = listOf(UserDto(1, "Ada Lovelace", "ada", "ada@example.com"))

        val result = repository.refresh()

        assertTrue(result.isSuccess)
        val cached = repository.observeUsers().first()
        assertEquals(1, cached.size)
        assertEquals("Ada Lovelace", cached.first().name)
    }

    @Test
    fun `a failed refresh does not clear an existing cache`() = runTest {
        api.usersToReturn = listOf(UserDto(1, "Ada Lovelace", "ada", "ada@example.com"))
        repository.refresh()

        api.errorToThrow = IOException("network down")
        val result = repository.refresh()

        assertTrue(result.isFailure)
        val cached = repository.observeUsers().first()
        assertEquals(1, cached.size)
    }

    @Test
    fun `refresh evicts users no longer present on the server`() = runTest {
        api.usersToReturn = listOf(
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
    fun `observeUser returns a single cached user by id`() = runTest {
        api.usersToReturn = listOf(
            UserDto(1, "Ada Lovelace", "ada", "ada@example.com"),
            UserDto(2, "Alan Turing", "alan", "alan@example.com"),
        )
        repository.refresh()

        val user = repository.observeUser(2).first()

        assertEquals("Alan Turing", user?.name)
    }
}
