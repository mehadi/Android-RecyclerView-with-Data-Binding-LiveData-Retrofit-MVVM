package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.UserApi
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto

class FakeUserApi : UserApi {
    var usersToReturn: List<UserDto> = emptyList()
    var errorToThrow: Throwable? = null

    override suspend fun getUsers(): List<UserDto> {
        errorToThrow?.let { throw it }
        return usersToReturn
    }
}
