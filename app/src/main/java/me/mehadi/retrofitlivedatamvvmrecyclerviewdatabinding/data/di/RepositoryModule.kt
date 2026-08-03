package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository.UserPreferencesRepositoryImpl
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.repository.UserRepositoryImpl
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserPreferencesRepository
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
