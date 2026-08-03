package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.presentation.userdetail

import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User

data class UserDetailUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
)
