package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Address
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
    @Embedded(prefix = "address_") val address: AddressEntity = AddressEntity(),
    val isFavorite: Boolean = false,
)

/** Flattened into the users table as `address_*` columns via [Embedded]. */
data class AddressEntity(
    val street: String = "",
    val suite: String = "",
    val city: String = "",
    val zipcode: String = "",
    val latitude: String = "",
    val longitude: String = "",
)

fun UserEntity.toDomain(): User =
    User(
        id = id,
        name = name,
        username = username,
        email = email,
        phone = phone,
        website = website,
        company = company,
        address =
            Address(
                street = address.street,
                suite = address.suite,
                city = address.city,
                zipcode = address.zipcode,
                latitude = address.latitude,
                longitude = address.longitude,
            ),
        isFavorite = isFavorite,
    )
