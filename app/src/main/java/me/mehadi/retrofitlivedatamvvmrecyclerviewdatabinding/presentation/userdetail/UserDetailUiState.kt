package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import androidx.annotation.StringRes
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Post
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Todo
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

data class UserDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val posts: List<Post> = emptyList(),
    val todos: List<Todo> = emptyList(),
    val albums: List<Album> = emptyList(),
    /** True only during the first content load, while nothing is cached yet. */
    val isContentLoading: Boolean = false,
    /** Refresh failure for the activity content; a snackbar when cached content exists, inline otherwise. */
    @StringRes val contentErrorRes: Int? = null,
) {
    val hasContent: Boolean
        get() = posts.isNotEmpty() || todos.isNotEmpty() || albums.isNotEmpty()
}
