package com.example.youtube.ui.theme.screen.userscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.youtube.domain.model.Video
import com.example.youtube.navigation.VideoDetails
import com.example.youtube.ui.theme.screen.homescreen.VideoIntent
import com.example.youtube.ui.theme.screen.homescreen.VideoViewModel
import com.example.youtube.ui.theme.screen.homescreen.formatValue
import com.example.youtube.ui.theme.screen.homescreen.toDuration

@Composable
fun UserScreen(
    viewModel: VideoViewModel,
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack
) {
    val watchedVideos by viewModel.watchedVideos.collectAsState()
    val favoriteVideos by viewModel.favoriteVideos.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF272727))
    ) {

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "История просмотров",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            if (watchedVideos.isEmpty()) {
                Text(
                    "Вы пока не посмотрели ни одного видео.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(watchedVideos) { video ->
                        UserListItem(video) { videoId ->
                            // Обработка клика по видео
                            viewModel.handleIntent(VideoIntent.SelectVideo(videoId))
                            navBackStack.add(VideoDetails(videoId))
                        }
                    }
                }
            }

            Text(text = "Избранное", color = Color.White)
            if (favoriteVideos.isEmpty()) {
                Text(
                    "У вас пока нет избранных видео.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(favoriteVideos) { video ->
                        UserListItem(video) { videoId ->
                            // Обработка клика по видео
                            viewModel.handleIntent(VideoIntent.SelectVideo(videoId))
                            navBackStack.add(VideoDetails(videoId))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    video: Video,
    onVideoClick: (String) -> Unit = {}
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Column(
        modifier = Modifier
            .size(180.dp)
            .background(Color(0xFF0f0f0f))
            .clickable { onVideoClick(video.id) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.posterUrl)
                        .size(coil.size.Size.ORIGINAL)
                        .build(),
                    imageLoader = imageLoader
                ),
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .width(60.dp)
                    .height(20.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatValue(video.duration.toDuration()),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = video.title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.author,
                    color = Color(0xFF838383),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${formatValue(video.views)} просмотров • ${video.publishedAt}",
                    color = Color(0xFF838383),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}