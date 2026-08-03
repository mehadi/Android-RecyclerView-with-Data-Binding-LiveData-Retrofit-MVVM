package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUserById(id: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavoriteUsers(): Flow<List<UserEntity>>

    @Query("SELECT id FROM users WHERE isFavorite = 1")
    suspend fun getFavoriteIds(): List<Int>

    @Query("UPDATE users SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Int, isFavorite: Boolean)

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Query("DELETE FROM users WHERE id NOT IN (:keepIds)")
    suspend fun deleteMissing(keepIds: List<Int>)

    @Query("DELETE FROM users")
    suspend fun clearAll()

    /** Replaces the cache with [users] atomically so observers never see a transient empty list. */
    @Transaction
    suspend fun replaceAll(users: List<UserEntity>) {
        if (users.isEmpty()) {
            clearAll()
        } else {
            upsertAll(users)
            deleteMissing(users.map { it.id })
        }
    }
}
