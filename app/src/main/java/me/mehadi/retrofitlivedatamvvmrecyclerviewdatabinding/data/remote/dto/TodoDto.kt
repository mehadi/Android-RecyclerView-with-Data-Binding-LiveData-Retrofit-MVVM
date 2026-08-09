package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto

import com.google.gson.annotations.SerializedName
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.TodoEntity

/** Wire format returned by the JSONPlaceholder `/todos` endpoint. */
data class TodoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("completed") val completed: Boolean?,
)

fun TodoDto.toEntity(): TodoEntity =
    TodoEntity(
        id = id,
        userId = userId,
        title = title.orEmpty(),
        isCompleted = completed ?: false,
    )
