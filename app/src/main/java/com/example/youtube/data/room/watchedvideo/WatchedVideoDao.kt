package com.example.youtube.data.room.watchedvideo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchedVideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(watchedVideo: WatchedVideo)

    @Query("SELECT * FROM watched_videos ORDER BY id DESC")
    suspend fun getAllWatchedVideos(): List<WatchedVideo>
}
