package com.example.youtube.domain.usecase

import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.domain.model.toVideo

interface AddToWatchedVideosUseCase {
    suspend operator fun invoke(videoId: String)
}

interface LoadWatchedVideosUseCase {
    suspend operator fun invoke()
}

class AddToWatchedVideosUseCaseImpl(private val repository: VideoRepository) :
    AddToWatchedVideosUseCase {
    override suspend fun invoke(videoId: String) {
        repository.addToWatchedVideos(videoId)
    }
}

class LoadWatchedVideosUseCaseImpl(private val repository: VideoRepository) :
    LoadWatchedVideosUseCase {
    override suspend fun invoke() {
        repository.getFavoriteVideos().map { it.toVideo() }
    }
}