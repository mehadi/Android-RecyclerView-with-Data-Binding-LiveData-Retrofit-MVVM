package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.AppDatabase
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local.UserDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "users.db")
            // This DB is purely a cache of network data, so a schema bump can safely wipe and
            // repopulate it on the next refresh instead of writing a real Migration.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
