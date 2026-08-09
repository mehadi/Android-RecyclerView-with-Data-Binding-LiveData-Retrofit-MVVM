package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo

@Entity(tableName = "todos", indices = [Index("userId")])
data class TodoEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val isCompleted: Boolean,
)

fun TodoEntity.toDomain(): Todo =
    Todo(
        id = id,
        userId = userId,
        title = title,
        isCompleted = isCompleted,
    )
