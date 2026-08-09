package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto

import com.google.gson.annotations.SerializedName
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.AlbumEntity

/** Wire format returned by the JSONPlaceholder `/albums` endpoint. */
data class AlbumDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String?,
)

fun AlbumDto.toEntity(): AlbumEntity =
    AlbumEntity(
        id = id,
        userId = userId,
        title = title.orEmpty(),
    )
