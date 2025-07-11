package com.example.youtube.ui.theme.screen.userscreen

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube.data.repository.VideoRepository
import com.example.youtube.domain.model.Video
import com.example.youtube.domain.model.toVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
) : ViewModel() {

    private val _watchedVideos = MutableStateFlow<List<Video>>(emptyList())
    val watchedVideos: StateFlow<List<Video>> = _watchedVideos.asStateFlow()

    private val _searchHistory = mutableStateListOf<Video>()
    val searchHistory: List<Video> = _searchHistory

    init {
        loadWatchedVideos()
    }

    private fun loadWatchedVideos() {
        viewModelScope.launch {
            _watchedVideos.value = videoRepository.loadWatchedVideos().map { it.toVideo() }
        }
    }

    fun getWatchedVideos(): List<Video> {
        return _watchedVideos.value
    }
}

