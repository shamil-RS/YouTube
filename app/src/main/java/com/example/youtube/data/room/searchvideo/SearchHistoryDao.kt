package com.example.youtube.data.room.searchvideo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory)

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    suspend fun getAllSearchHistory(): List<SearchHistory>

    @Query("DELETE FROM search_history WHERE videoId = :videoId")
    suspend fun deleteByVideoId(videoId: String)
}

