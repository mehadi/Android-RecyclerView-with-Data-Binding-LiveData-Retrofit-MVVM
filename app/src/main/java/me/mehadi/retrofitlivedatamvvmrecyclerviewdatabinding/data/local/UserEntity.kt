package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String = "",
    val website: String = "",
    val company: String = "",
    val isFavorite: Boolean = false,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website,
    company = company,
    isFavorite = isFavorite,
)
