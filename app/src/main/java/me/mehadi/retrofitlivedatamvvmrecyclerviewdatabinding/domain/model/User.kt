package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model

/**
 * Immutable domain representation of a user, decoupled from both the network DTO
 * and the Room entity so either can change shape without rippling into the UI layer.
 */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String = "",
    val website: String = "",
    val company: String = "",
    val address: Address = Address(),
    val isFavorite: Boolean = false,
) {
    val displayName: String
        get() = name.ifBlank { username.ifBlank { "Unknown user" } }

    val initials: String
        get() =
            displayName
                .trim()
                .split(" ")
                .filter { it.isNotBlank() }
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString(separator = "")
                .ifBlank { "?" }
}

/** Postal address for a [User]; fields are blank (never null) when the server omits them. */
data class Address(
    val street: String = "",
    val suite: String = "",
    val city: String = "",
    val zipcode: String = "",
    val latitude: String = "",
    val longitude: String = "",
) {
    /** Display-ready one-liner, e.g. "Kulas Light, Apt. 556, Gwenborough 92998-3874"; blank parts are skipped. */
    val singleLine: String
        get() =
            listOf(street, suite, listOf(city, zipcode).filter { it.isNotBlank() }.joinToString(" "))
                .filter { it.isNotBlank() }
                .joinToString(", ")

    val hasCoordinates: Boolean
        get() = latitude.isNotBlank() && longitude.isNotBlank()
}
