package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

data class UserDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
)
