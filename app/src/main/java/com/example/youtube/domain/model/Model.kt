package com.example.youtube.domain.model

import com.example.youtube.data.room.favorite.FavoriteVideo
import com.example.youtube.data.room.watchedvideo.WatchedVideo
import kotlinx.serialization.Serializable

@Serializable
data class YouTubeSearchResponse(
    val kind: String,
    val items: List<YouTubeVideoItem>
)

@Serializable
data class YouTubeVideoItem(
    val kind: String,
    val id: VideoId,
    val snippet: Snippet
)

@Serializable
data class YouTubeVideoDetailsResponse(
    val kind: String,
    val items: List<VideoDetailsItem>
)

@Serializable
data class VideoDetailsItem(
    val id: String,
    val contentDetails: ContentDetails,
    val statistics: VideoStatistics,
    val snippet: Snippet?,
)

@Serializable
data class ContentDetails(
    val duration: String
)

@Serializable
data class VideoStatistics(
    val viewCount: Int,
    val likeCount: Int? = null,
    val dislikeCount: Int? = null
)

@Serializable
data class VideoId(
    val kind: String,
    val videoId: String
)

@Serializable
data class Snippet(
    val title: String,
    val categoryId: String? = null,
    val thumbnails: Thumbnails,
    val channelTitle: String? = null,
    val publishedAt: String? = null
)

@Serializable
data class Thumbnails(
    val default: Thumbnail,
    val medium: Thumbnail? = null,
    val high: Thumbnail? = null,
    val standard: Thumbnail? = null,
    val maxres: Thumbnail? = null
)

@Serializable
data class Thumbnail(
    val url: String
)

data class VideoUIState(
    val listVideo: List<Video> = emptyList()
)

@Serializable
data class Video(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val addedAt: String = "",
    val publishedAt: String = "",
    val category: String = "",
    val duration: Int = 0,
    val views: Int = 0,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val posterUrl: String = "",
    var isSelected: Boolean = false,
)

fun WatchedVideo.toVideo(): Video {
    return Video(
        id = this.id,
        title = this.title,
        category = this.category,
        duration = this.duration,
        views = this.views,
        likes = this.likes,
        dislikes = this.dislikes,
        posterUrl = this.posterUrl
    )
}

fun FavoriteVideo.toVideo(): Video {
    return Video(
        id = videoId,
        title = title,
        author = author,
        posterUrl = posterUrl,
        isSelected = true
    )
}