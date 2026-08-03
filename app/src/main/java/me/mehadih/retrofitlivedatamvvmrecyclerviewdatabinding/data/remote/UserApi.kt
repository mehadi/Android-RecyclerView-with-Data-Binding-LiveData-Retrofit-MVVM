package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
