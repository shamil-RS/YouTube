package com.example.youtube.data.room.favorite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteVideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteVideo: FavoriteVideo)

    @Delete
    suspend fun delete(favoriteVideo: FavoriteVideo)

    @Query("SELECT * FROM favorite_videos")
    suspend fun getAllFavorites(): List<FavoriteVideo>
}