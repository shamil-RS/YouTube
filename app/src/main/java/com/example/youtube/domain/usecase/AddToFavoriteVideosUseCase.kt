package com.example.youtube.domain.usecase

import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.data.room.favorite.FavoriteVideo
import com.example.youtube.domain.model.Video

class AddFavoriteVideoUseCase(private val repository: VideoRepository) {
    suspend operator fun invoke(video: Video) {
        repository.addFavorite(video)
    }
}

class RemoveFavoriteVideoUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(video: Video) {
        videoRepository.removeFavorite(video)
    }
}

class GetFavoriteVideosUseCase(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(): List<FavoriteVideo> {
        return videoRepository.getFavoriteVideos()
    }
}