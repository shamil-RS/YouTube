package com.example.youtube.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.domain.model.Video

class RecommendVideosUseCase(private val repository: VideoRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        selectedVideo: Video,
        initialVideos: List<Video>,
        query: String,
        maxResults: Int = 50
    ): List<Video> {
        return repository.recommendVideos(selectedVideo, initialVideos, query, maxResults)
    }
}