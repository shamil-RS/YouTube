package com.example.youtube.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.youtube.data.api.YouTubeApiClient
import com.example.youtube.data.room.favorite.FavoriteVideo
import com.example.youtube.data.room.favorite.FavoriteVideoDao
import com.example.youtube.data.room.searchvideo.SearchHistory
import com.example.youtube.data.room.searchvideo.SearchHistoryDao
import com.example.youtube.data.room.watchedvideo.WatchedVideo
import com.example.youtube.data.room.watchedvideo.WatchedVideoDao
import com.example.youtube.domain.model.Video

class VideoRepository(
    private val apiClient: YouTubeApiClient,
    private val watchedVideoDao: WatchedVideoDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val favoriteVideoDao: FavoriteVideoDao
) {
    private val watchedVideos = mutableListOf<String>()

    suspend fun addFavorite(video: Video) {
        val favoriteVideo = FavoriteVideo(
            videoId = video.id,
            title = video.title,
            author = video.author,
            posterUrl = video.posterUrl,
        )
        favoriteVideoDao.insert(favoriteVideo)
    }

    suspend fun removeFavorite(video: Video) {
        val favoriteVideo = FavoriteVideo(
            videoId = video.id,
            title = video.title,
            author = video.author,
            posterUrl = video.posterUrl,
        )
        favoriteVideoDao.delete(favoriteVideo)
    }

    suspend fun getFavoriteVideos(): List<FavoriteVideo> {
        return favoriteVideoDao.getAllFavorites()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun searchVideos(query: String, maxResults: Int = 50): List<Video> {
        return apiClient.searchVideos(query, maxResults)
    }

    fun addToWatchedVideos(videoId: String) {
        if (!watchedVideos.contains(videoId)) {
            watchedVideos.add(videoId)
        }
    }

    suspend fun saveWatchedVideo(video: WatchedVideo) {
        watchedVideoDao.insert(video)
    }

    suspend fun loadWatchedVideos(): List<WatchedVideo> {
        return watchedVideoDao.getAllWatchedVideos()
    }

    suspend fun saveSearchHistory(video: Video) {
        val searchHistory = SearchHistory(
            videoId = video.id,
            title = video.title,
            author = video.author,
            posterUrl = video.posterUrl
        )
        searchHistoryDao.insert(searchHistory)
    }

    suspend fun loadSearchHistory(): List<SearchHistory> {
        return searchHistoryDao.getAllSearchHistory()
    }

    suspend fun deleteSearchHistory(videoId: String) {
        searchHistoryDao.deleteByVideoId(videoId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun recommendVideos(
        selectedVideo: Video,
        initialVideos: List<Video>,
        query: String,
        maxResults: Int = 50
    ): List<Video> {
        return apiClient.recommendVideos(selectedVideo, initialVideos, query, maxResults)
    }
}