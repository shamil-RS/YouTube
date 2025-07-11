package com.example.youtube.ui.theme.screen.homescreen

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.data.room.watchedvideo.WatchedVideo
import com.example.youtube.domain.model.Video
import com.example.youtube.domain.model.VideoUIState
import com.example.youtube.domain.model.toVideo
import com.example.youtube.domain.usecase.AddFavoriteVideoUseCase
import com.example.youtube.domain.usecase.AddToWatchedVideosUseCase
import com.example.youtube.domain.usecase.DeleteSearchHistoryUseCase
import com.example.youtube.domain.usecase.GetFavoriteVideosUseCase
import com.example.youtube.domain.usecase.LoadSearchHistoryUseCase
import com.example.youtube.domain.usecase.RecommendVideosUseCase
import com.example.youtube.domain.usecase.RemoveFavoriteVideoUseCase
import com.example.youtube.domain.usecase.SearchVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VideoState {
    data object Idle : VideoState()
    data object Loading : VideoState()
    data class Error(val error: String) : VideoState()
    data class VideosLoaded(val videos: List<Video>) : VideoState()
    data class VideoSelected(val video: Video, val recommendedVideos: List<Video>) : VideoState()
}

sealed class VideoIntent {
    data object LoadVideos : VideoIntent()
    data class SelectVideo(val videoId: String) : VideoIntent()
    data class SearchVideos(val query: String) : VideoIntent()
}

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val searchVideosUseCase: SearchVideosUseCase,
    private val recommendVideosUseCase: RecommendVideosUseCase,
    private val addToWatchedVideosUseCase: AddToWatchedVideosUseCase,
    private val videoRepository: VideoRepository,
    private val loadSearchHistoryUseCase: LoadSearchHistoryUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase,
    private val addFavoriteVideoUseCase: AddFavoriteVideoUseCase,
    private val removeFavoriteVideoUseCase: RemoveFavoriteVideoUseCase,
    private val getFavoriteVideosUseCase: GetFavoriteVideosUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<VideoState>(VideoState.Idle)
    val state: StateFlow<VideoState> = _state.asStateFlow()

    private val _watchedVideos = MutableStateFlow<List<Video>>(emptyList())
    val watchedVideos: StateFlow<List<Video>> = _watchedVideos.asStateFlow()

    private val _searchHistory = mutableStateListOf<Video>()
    val searchHistory: List<Video> = _searchHistory

    private val _favoriteVideos = MutableStateFlow<List<Video>>(emptyList())
    val favoriteVideos: StateFlow<List<Video>> = _favoriteVideos.asStateFlow()

    private val videos = MutableStateFlow(VideoUIState())

    fun handleIntent(intent: VideoIntent) {
        when (intent) {
            is VideoIntent.LoadVideos -> loadVideos()
            is VideoIntent.SelectVideo -> selectVideo(intent.videoId)
            is VideoIntent.SearchVideos -> searchVideos(intent.query)
        }
    }

    init {
        loadWatchedVideos()
        loadSearchHistory()
        loadFavoriteVideos()
    }

    private fun loadFavoriteVideos() {
        viewModelScope.launch {
            val favorites = getFavoriteVideosUseCase()
            _favoriteVideos.value = favorites.map { it.toVideo() }
            // Обновляем состояние всех видео, чтобы отобразить актуальные данные о избранном
            videos.value = videos.value.copy(
                listVideo = videos.value.listVideo.map { video ->
                    video.copy(isSelected = favorites.any { it.videoId == video.id })
                }
            )
        }
    }

    fun resetState() {
        _state.value = VideoState.Idle
    }

    fun setFavorite(video: Video) {
        viewModelScope.launch {
            // Проверяем, находится ли видео уже в избранном
            val isFavorite = _favoriteVideos.value.any { it.id == video.id }

            if (isFavorite) {
                // Если видео уже в избранном, убираем его
                removeFavoriteVideoUseCase(video)
                video.isSelected = false
            } else {
                // Если видео не в избранном, добавляем его
                addFavoriteVideoUseCase(video)
                video.isSelected = true
            }

            // Обновляем список избранных и состояние видео
            loadFavoriteVideos()
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            val history = loadSearchHistoryUseCase()
            _searchHistory.clear()
            _searchHistory.addAll(history)
        }
    }

    fun deleteSearchHistory(video: Video) {
        viewModelScope.launch {
            deleteSearchHistoryUseCase(video.id)
            _searchHistory.remove(video)
        }
    }

    private fun loadWatchedVideos() {
        viewModelScope.launch {
            _watchedVideos.value = videoRepository.loadWatchedVideos().map { it.toVideo() }
        }
    }

    @SuppressLint("NewApi")
    private fun loadVideos() {
        _state.value = VideoState.Loading
        viewModelScope.launch {
            try {
                val videos = searchVideosUseCase("")
                _state.value = VideoState.VideosLoaded(videos)
            } catch (e: Exception) {
                _state.value = VideoState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun selectVideo(videoId: String) {
        viewModelScope.launch {
            val videos = (_state.value as? VideoState.VideosLoaded)?.videos
                ?: (_state.value as? VideoState.VideoSelected)?.recommendedVideos
                ?: return@launch
            val selectedVideo = videos.find { it.id == videoId }
            selectedVideo?.let {
                addToWatchedVideosUseCase(videoId) // Добавление в историю просмотров
                if (!_watchedVideos.value.any { video -> video.id == it.id }) { // Проверка, чтобы избежать дублирования
                    // Сохраняем в базу данных
                    videoRepository.saveWatchedVideo(
                        WatchedVideo(
                            id = it.id,
                            title = it.title,
                            category = it.category,
                            duration = it.duration,
                            views = it.views,
                            likes = it.likes,
                            dislikes = it.dislikes,
                            posterUrl = it.posterUrl
                        )
                    )
                    _watchedVideos.value += it
                }

                val recommendedVideos = recommendVideosUseCase(it, videos, it.title)
                _state.value = VideoState.VideoSelected(it, recommendedVideos)
            }
        }
    }

    @SuppressLint("NewApi")
    private fun searchVideos(query: String) {
        viewModelScope.launch {
            _state.value = VideoState.Loading
            try {
                val videos = searchVideosUseCase(query)

                // Храним информацию о первом видео каждого автора
                val authorToFirstVideoMap = mutableMapOf<String, Video>()

                for (video in videos) {
                    if (!authorToFirstVideoMap.containsKey(video.author)) {
                        authorToFirstVideoMap[video.author] = video
                    }
                }

                // Проверяем, является ли запрос конкретным автором
                if (videos.isNotEmpty()) {
                    val firstAuthor = videos.first().author
                    val firstVideoForAuthor = authorToFirstVideoMap[firstAuthor]
                    if (firstVideoForAuthor != null && !_searchHistory.any { it.author == firstAuthor }) {
                        _searchHistory.add(firstVideoForAuthor)
                        videoRepository.saveSearchHistory(firstVideoForAuthor)
                    }
                }

                _state.value = VideoState.VideosLoaded(videos)
            } catch (e: Exception) {
                _state.value = VideoState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun getWatchedVideos(): List<Video> {
        return _watchedVideos.value
    }
}
