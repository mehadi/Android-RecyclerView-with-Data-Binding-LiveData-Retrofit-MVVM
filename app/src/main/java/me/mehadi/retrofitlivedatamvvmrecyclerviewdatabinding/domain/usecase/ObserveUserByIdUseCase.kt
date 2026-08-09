package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

class ObserveUserByIdUseCase
    @Inject
    constructor(
        private val repository: UserRepository,
    ) {
        operator fun invoke(id: Int): Flow<User?> = repository.observeUser(id)
    }
