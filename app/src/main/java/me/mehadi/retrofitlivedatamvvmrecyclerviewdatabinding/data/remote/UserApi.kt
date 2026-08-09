package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.AlbumDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.PostDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.TodoDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("posts")
    suspend fun getPosts(
        @Query("userId") userId: Int,
    ): List<PostDto>

    @GET("albums")
    suspend fun getAlbums(
        @Query("userId") userId: Int,
    ): List<AlbumDto>

    @GET("todos")
    suspend fun getTodos(
        @Query("userId") userId: Int,
    ): List<TodoDto>
}
