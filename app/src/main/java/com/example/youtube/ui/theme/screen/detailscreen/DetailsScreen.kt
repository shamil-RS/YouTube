package com.example.youtube.ui.theme.screen.detailscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.example.youtube.ui.theme.screen.homescreen.LoadingScreen
import com.example.youtube.ui.theme.screen.homescreen.VideoIntent
import com.example.youtube.ui.theme.screen.homescreen.VideoPlayer
import com.example.youtube.ui.theme.screen.homescreen.VideoState
import com.example.youtube.ui.theme.screen.homescreen.VideoViewModel

@Composable
fun VideoDetailsScreen(
    viewModel: VideoViewModel,
    videoId: String,
    navBackStack: NavBackStack
) {
    LaunchedEffect(videoId) {
        viewModel.handleIntent(VideoIntent.SelectVideo(videoId))
    }

    when (val state = viewModel.state.collectAsState().value) {
        is VideoState.VideoSelected -> {
            VideoPlayer(state.video, state.recommendedVideos, viewModel, navBackStack)
        }

        is VideoState.Loading -> LoadingScreen()
        is VideoState.Error -> Text("Error: ${state.error}", modifier = Modifier.padding(20.dp))
        else -> {}
    }
}