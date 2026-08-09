package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.UserApi
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.AlbumDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.PostDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.TodoDto
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto.UserDto

class FakeUserApi : UserApi {
    var usersToReturn: List<UserDto> = emptyList()
    var postsToReturn: List<PostDto> = emptyList()
    var albumsToReturn: List<AlbumDto> = emptyList()
    var todosToReturn: List<TodoDto> = emptyList()
    var errorToThrow: Throwable? = null

    /** Per-endpoint failures, so a single content endpoint can fail while its siblings succeed. */
    var postsErrorToThrow: Throwable? = null
    var albumsErrorToThrow: Throwable? = null
    var todosErrorToThrow: Throwable? = null

    override suspend fun getUsers(): List<UserDto> {
        errorToThrow?.let { throw it }
        return usersToReturn
    }

    override suspend fun getPosts(userId: Int): List<PostDto> {
        errorToThrow?.let { throw it }
        postsErrorToThrow?.let { throw it }
        return postsToReturn.filter { it.userId == userId }
    }

    override suspend fun getAlbums(userId: Int): List<AlbumDto> {
        errorToThrow?.let { throw it }
        albumsErrorToThrow?.let { throw it }
        return albumsToReturn.filter { it.userId == userId }
    }

    override suspend fun getTodos(userId: Int): List<TodoDto> {
        errorToThrow?.let { throw it }
        todosErrorToThrow?.let { throw it }
        return todosToReturn.filter { it.userId == userId }
    }
}
