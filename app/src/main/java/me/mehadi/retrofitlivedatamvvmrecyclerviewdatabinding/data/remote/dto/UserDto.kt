package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto

import com.google.gson.annotations.SerializedName
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserEntity

/** Wire format returned by the JSONPlaceholder `/users` endpoint. */
data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("company") val company: CompanyDto? = null,
)

/** Nested `company` object on the JSONPlaceholder user payload; only the display name is needed here. */
data class CompanyDto(
    @SerializedName("name") val name: String?,
)

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name.orEmpty(),
    username = username.orEmpty(),
    email = email.orEmpty(),
    phone = phone.orEmpty(),
    website = website.orEmpty(),
    company = company?.name.orEmpty(),
)
