package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.common

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

/**
 * Client-side search over a cached user list, shared by the Home and Favorites screens.
 * Matches on name, username, or email (case-insensitive); a blank query matches everyone.
 */
internal fun filterUsers(
    users: List<User>,
    query: String,
): List<User> {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return users
    return users.filter { user ->
        user.name.contains(trimmed, ignoreCase = true) ||
            user.username.contains(trimmed, ignoreCase = true) ||
            user.email.contains(trimmed, ignoreCase = true)
    }
}
