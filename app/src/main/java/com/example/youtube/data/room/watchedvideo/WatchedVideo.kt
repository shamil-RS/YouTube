package com.example.youtube.data.room.watchedvideo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watched_videos")
data class WatchedVideo(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val duration: Int,
    val views: Int,
    val likes: Int,
    val dislikes: Int,
    val posterUrl: String
)