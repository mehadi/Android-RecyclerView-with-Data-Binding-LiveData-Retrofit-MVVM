package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model

data class Todo(
    val id: Int,
    val userId: Int,
    val title: String,
    val isCompleted: Boolean,
)
