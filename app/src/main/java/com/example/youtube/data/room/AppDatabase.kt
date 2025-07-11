package com.example.youtube.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.youtube.data.room.favorite.FavoriteVideo
import com.example.youtube.data.room.favorite.FavoriteVideoDao
import com.example.youtube.data.room.searchvideo.SearchHistory
import com.example.youtube.data.room.searchvideo.SearchHistoryDao
import com.example.youtube.data.room.watchedvideo.WatchedVideo
import com.example.youtube.data.room.watchedvideo.WatchedVideoDao

@Database(
    entities = [WatchedVideo::class, SearchHistory::class, FavoriteVideo::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchedVideoDao(): WatchedVideoDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun favoriteVideoDao(): FavoriteVideoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
