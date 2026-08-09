package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post

@Entity(tableName = "posts", indices = [Index("userId")])
data class PostEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
)

fun PostEntity.toDomain(): Post =
    Post(
        id = id,
        userId = userId,
        title = title,
        body = body,
    )
