package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface UserContentDao {
    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY id ASC")
    fun observePostsForUser(userId: Int): Flow<List<PostEntity>>

    @Query("SELECT * FROM albums WHERE userId = :userId ORDER BY id ASC")
    fun observeAlbumsForUser(userId: Int): Flow<List<AlbumEntity>>

    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY id ASC")
    fun observeTodosForUser(userId: Int): Flow<List<TodoEntity>>

    @Query("DELETE FROM posts WHERE userId = :userId")
    suspend fun deletePostsForUser(userId: Int)

    @Query("DELETE FROM albums WHERE userId = :userId")
    suspend fun deleteAlbumsForUser(userId: Int)

    @Query("DELETE FROM todos WHERE userId = :userId")
    suspend fun deleteTodosForUser(userId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoEntity>)

    /** Replaces one user's cached posts atomically so observers never see partial state. */
    @Transaction
    suspend fun replacePostsForUser(
        userId: Int,
        posts: List<PostEntity>,
    ) {
        deletePostsForUser(userId)
        insertPosts(posts)
    }

    /** Replaces one user's cached albums atomically so observers never see partial state. */
    @Transaction
    suspend fun replaceAlbumsForUser(
        userId: Int,
        albums: List<AlbumEntity>,
    ) {
        deleteAlbumsForUser(userId)
        insertAlbums(albums)
    }

    /** Replaces one user's cached todos atomically so observers never see partial state. */
    @Transaction
    suspend fun replaceTodosForUser(
        userId: Int,
        todos: List<TodoEntity>,
    ) {
        deleteTodosForUser(userId)
        insertTodos(todos)
    }
}
