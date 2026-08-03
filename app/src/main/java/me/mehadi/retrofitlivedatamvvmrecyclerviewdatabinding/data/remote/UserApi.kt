package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
