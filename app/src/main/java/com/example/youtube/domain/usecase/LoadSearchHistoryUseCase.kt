package com.example.youtube.domain.usecase

import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.domain.model.Video

class LoadSearchHistoryUseCase(private val repository: VideoRepository) {
    suspend operator fun invoke(): List<Video> {
        return repository.loadSearchHistory().map {
            Video(it.videoId, it.title, it.author, it.posterUrl)
        }
    }
}