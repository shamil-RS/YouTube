package com.example.youtube.data.room.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_videos")
data class FavoriteVideo(
    @PrimaryKey
    val videoId: String,
    val title: String,
    val author: String,
    val posterUrl: String
)