package com.example.youtube.domain.usecase

import com.example.youtube.data.repository.VideoRepository

class DeleteSearchHistoryUseCase(private val repository: VideoRepository) {
    suspend operator fun invoke(videoId: String) {
        repository.deleteSearchHistory(videoId)
    }
}