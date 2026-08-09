package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto

import com.google.gson.annotations.SerializedName
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.PostEntity

/** Wire format returned by the JSONPlaceholder `/posts` endpoint. */
data class PostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
)

fun PostDto.toEntity(): PostEntity =
    PostEntity(
        id = id,
        userId = userId,
        title = title.orEmpty(),
        body = body.orEmpty(),
    )
