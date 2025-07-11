package com.example.youtube.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.domain.model.Video

class SearchVideosUseCase(private val repository: VideoRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(query: String): List<Video> {
        return repository.searchVideos(query, maxResults = 50)
    }
}