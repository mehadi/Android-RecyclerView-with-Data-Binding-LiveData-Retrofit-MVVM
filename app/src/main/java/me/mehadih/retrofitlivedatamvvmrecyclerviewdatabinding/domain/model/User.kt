package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model

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
    val isFavorite: Boolean = false,
) {
    val displayName: String
        get() = name.ifBlank { username.ifBlank { "Unknown user" } }

    val initials: String
        get() = displayName
            .trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString(separator = "")
            .ifBlank { "?" }
}
