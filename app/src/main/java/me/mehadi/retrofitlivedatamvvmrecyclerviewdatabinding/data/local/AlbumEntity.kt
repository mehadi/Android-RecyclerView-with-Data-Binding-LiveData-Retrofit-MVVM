package me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.mehadi.retrofitlivedatamvvmrecyclerviewdatabinding.domain.model.Album

@Entity(tableName = "albums", indices = [Index("userId")])
data class AlbumEntity(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
)

fun AlbumEntity.toDomain(): Album =
    Album(
        id = id,
        userId = userId,
        title = title,
    )
