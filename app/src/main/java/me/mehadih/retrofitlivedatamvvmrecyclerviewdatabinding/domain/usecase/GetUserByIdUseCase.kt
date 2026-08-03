package me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.User
import me.mehadih.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    operator fun invoke(id: Int): Flow<User?> = repository.observeUser(id)
}
