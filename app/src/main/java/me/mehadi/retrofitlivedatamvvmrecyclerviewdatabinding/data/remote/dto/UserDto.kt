package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.remote.dto

import com.google.gson.annotations.SerializedName
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.AddressEntity
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
    @SerializedName("address") val address: AddressDto? = null,
)

/** Nested `company` object on the JSONPlaceholder user payload; only the display name is needed here. */
data class CompanyDto(
    @SerializedName("name") val name: String?,
)

/** Nested `address` object on the JSONPlaceholder user payload. */
data class AddressDto(
    @SerializedName("street") val street: String?,
    @SerializedName("suite") val suite: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("zipcode") val zipcode: String?,
    @SerializedName("geo") val geo: GeoDto? = null,
)

/** Nested `geo` object on the address payload; JSONPlaceholder serializes coordinates as strings. */
data class GeoDto(
    @SerializedName("lat") val lat: String?,
    @SerializedName("lng") val lng: String?,
)

fun UserDto.toEntity(): UserEntity =
    UserEntity(
        id = id,
        name = name.orEmpty(),
        username = username.orEmpty(),
        email = email.orEmpty(),
        phone = phone.orEmpty(),
        website = website.orEmpty(),
        company = company?.name.orEmpty(),
        address =
            AddressEntity(
                street = address?.street.orEmpty(),
                suite = address?.suite.orEmpty(),
                city = address?.city.orEmpty(),
                zipcode = address?.zipcode.orEmpty(),
                latitude = address?.geo?.lat.orEmpty(),
                longitude = address?.geo?.lng.orEmpty(),
            ),
    )
