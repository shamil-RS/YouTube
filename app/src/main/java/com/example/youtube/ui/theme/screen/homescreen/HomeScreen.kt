package com.example.youtube.ui.theme.screen.homescreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation3.runtime.NavBackStack
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.youtube.R
import com.example.youtube.domain.model.Video
import com.example.youtube.navigation.VideoDetails
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@SuppressLint("RememberReturnType")
@Composable
fun VideoScreen(
    viewModel: VideoViewModel,
    navBackStack: NavBackStack,
    modifier: Modifier = Modifier
) {
    val searchHistory = viewModel.searchHistory // Получаем историю из ViewModel
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var topAppBar by remember { mutableStateOf(true) }
    val searchIconScale = remember { Animatable(1f) }
    val searchFieldWidth = remember { Animatable(0f) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var videoToDelete by remember { mutableStateOf<Video?>(null) }

    // Запускаем анимацию при изменении состояния isSearching
    LaunchedEffect(isSearching) {
        if (isSearching) {
            searchIconScale.animateTo(0f) // Скрываем иконку
            searchFieldWidth.animateTo(300f)
        } else {
            searchIconScale.animateTo(1f)
            searchFieldWidth.animateTo(0f)
        }
    }

    // Сброс состояния при инициализации экрана
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    Column(modifier = modifier.background(Color(0xFF0f0f0f))) {
        AnimatedVisibility(visible = topAppBar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = R.drawable.youtube),
                    modifier = Modifier.size(26.dp),
                    contentDescription = null
                )
                Text(
                    text = "YouTube",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.Search,
                    modifier = Modifier
                        .size(24.dp)
                        .scale(searchIconScale.value)
                        .clickable {
                            topAppBar = false
                            isSearching = true // Запускаем анимацию
                        },
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }

        // Переход к экрану поиска
        AnimatedVisibility(visible = isSearching) {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxSize()
                    .background(Color(0xFF0f0f0f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    // Поле ввода для поиска
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    topAppBar = true
                                    isSearching = false
                                },
                            tint = Color.White,
                            contentDescription = null
                        )
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .size(50.dp)
                                .width(searchFieldWidth.value.dp) // Преобразуем Float в Dp
                                .clip(RoundedCornerShape(26.dp))
                                .animateContentSize(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done // Устанавливаем действие на клавиатуре как "Done"
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (searchQuery.isNotEmpty()) {
                                        viewModel.handleIntent(VideoIntent.SearchVideos(searchQuery))
                                        isSearching = false
                                        topAppBar = true
                                        searchQuery = ""
                                    }
                                }
                            ),
                            textStyle = TextStyle(fontSize = 12.sp),
                            placeholder = { Text("Search video...", fontSize = 12.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF272727),
                                unfocusedContainerColor = Color(0xFF272727),
                                cursorColor = Color.White,
                                focusedTextColor = Color(0xFFaeaeae),
                                disabledTextColor = Color(0xFFaeaeae),
                                focusedIndicatorColor = Color(0xFF272727),
                                unfocusedIndicatorColor = Color(0xFF272727),
                                focusedLabelColor = Color(0xFF272727),
                                unfocusedLabelColor = Color(0xFF272727)
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    searchQuery = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            },
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF272727)),
                            contentAlignment = Alignment.Center,
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_keyboard_voice_24),
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            }
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(searchHistory) { historyItem ->
                            Row(
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(onLongPress = {
                                            // Устанавливаем видео для удаления и показываем диалог
                                            videoToDelete = historyItem
                                            showDeleteDialog = true
                                        })
                                    }
                                    .clickable {
                                        searchQuery =
                                            historyItem.author // Установить выбранный элемент в поле поиска
                                        viewModel.handleIntent(VideoIntent.SearchVideos(historyItem.author))
                                        isSearching = false
                                        topAppBar = true
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.timer),
                                    modifier = Modifier.size(22.dp),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                                Text(
                                    text = historyItem.author,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Box(
                                    modifier = Modifier
                                        .width(26.dp)
                                        .height(42.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = historyItem.posterUrl),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = null
                                    )
                                }
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.leftarrow),
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }

        // Диалог для подтверждения удаления
        if (showDeleteDialog && videoToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Подтверждение удаления") },
                text = { Text("Вы уверены, что хотите удалить '${videoToDelete!!.author}' из истории?") },
                confirmButton = {
                    Button(onClick = {
                        videoToDelete?.let {
                            viewModel.deleteSearchHistory(it)
                        }
                        showDeleteDialog = false
                        videoToDelete = null
                    }) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDeleteDialog = false
                        videoToDelete = null
                    }) {
                        Text("Отмена")
                    }
                }
            )
        }

        when (val state = viewModel.state.collectAsState().value) {
            is VideoState.Idle -> {
                viewModel.handleIntent(VideoIntent.LoadVideos)
            }

            is VideoState.Loading -> LoadingScreen()
            is VideoState.VideosLoaded -> VideoList(
                videos = state.videos.map { video ->
                    video.copy(isSelected = viewModel.favoriteVideos.value.any { it.id == video.id })
                },
                onVideoClick = { videoId ->
                    viewModel.handleIntent(VideoIntent.SelectVideo(videoId))
                    navBackStack.add(VideoDetails(videoId))
                },
                onFavoriteClick = { video ->
                    viewModel.setFavorite(video) // Добавление видео в избранное
                }
            )

            is VideoState.Error -> Text("Error: ${state.error}", modifier = Modifier.padding(20.dp))
            is VideoState.VideoSelected -> VideoPlayer(
                state.video,
                state.recommendedVideos,
                viewModel,
                navBackStack
            )
        }
    }
}

@Composable
fun VideoPlayer(
    video: Video,
    recommendedVideos: List<Video>,
    viewModel: VideoViewModel,
    navBackStack: NavBackStack
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0f0f0f))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        YouTubePlayerScreen(
            videoId = video.id,
            onBack = { navBackStack.removeLastOrNull() }
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = video.title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp, start = 10.dp)
        )

        Text(
            text = video.author,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "${formatValue(video.views)} просмотров • ${video.publishedAt}",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        StatisticVideo(likes = video.likes)

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalDivider(color = Color.Gray)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Recommended Videos",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column {
            recommendedVideos.forEach { recommendedVideo ->
                ListItem(recommendedVideo) { videoId ->
                    viewModel.handleIntent(VideoIntent.SelectVideo(videoId))
                    navBackStack.add(VideoDetails(videoId))
                }
            }
        }
    }
}

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun YouTubePlayerScreen(
    videoId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var playbackPosition by rememberSaveable { mutableFloatStateOf(0f) }
    val orientation = remember { mutableIntStateOf(context.resources.configuration.orientation) }

    val configuration = LocalConfiguration.current
    LaunchedEffect(configuration) {
        orientation.intValue = configuration.orientation
    }

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val window = (LocalView.current.context as ComponentActivity).window
    SideEffect {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.statusBars())
            hide(WindowInsetsCompat.Type.navigationBars())
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            WindowCompat.getInsetsController(window, window.decorView).apply {
                show(WindowInsetsCompat.Type.statusBars())
                show(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val youTubePlayer: YouTubePlayer? = null
    val isWifiConnected = remember { mutableStateOf(checkIfWifiConnected(context)) }

    // Создаем StateFlow для отслеживания состояния YouTubePlayer
    val playerStateFlow = remember { MutableStateFlow<YouTubePlayer?>(null) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier
                .then(if (isLandscape) Modifier.fillMaxHeight() else Modifier.fillMaxWidth())
                .aspectRatio(16f / 9f)
                .align(Alignment.Center),
            factory = { context ->
                YouTubePlayerView(context).apply {
                    coroutineScope.launch {
                        playerStateFlow.collect {
                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(videoId, playbackPosition)

                                    if (isWifiConnected.value) {
                                        coroutineScope.launch {
                                            delay(500)
                                            youTubePlayer.loadVideo(videoId, playbackPosition)
                                        }
                                    }
                                }

                                override fun onCurrentSecond(
                                    youTubePlayer: YouTubePlayer,
                                    second: Float
                                ) {
                                    playbackPosition = second
                                }
                            })
                        }
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .background(Color.White.copy(alpha = 0.1f), shape = CircleShape)
                .clickable {
                    youTubePlayer?.pause() // Останавливаем видео
                    onBack()
                }
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun checkIfWifiConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork?.let {
        connectivityManager.getNetworkCapabilities(it)
    }
    return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
}

@Composable
fun VideoList(
    videos: List<Video>,
//    isSelected: Boolean = false,
    onVideoClick: (String) -> Unit,
    onFavoriteClick: (Video) -> Unit
) {
    LazyColumn {
        items(videos) { video ->
            ListItem(video, onFavoriteClick, onVideoClick)
        }
    }
}

@Composable
fun ListItem(
    video: Video,
//    isSelected: Boolean = false,
    onFavoriteClick: (Video) -> Unit = {},
    onVideoClick: (String) -> Unit = {},
) {

//    val isSelected = remember { mutableStateOf(video.isSelected) }
    val favoriteIcon =
        if (video.isSelected) Icons.Default.Favorite else Icons.Default.FavoriteBorder

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0f0f0f))
            .clickable {
                onVideoClick(video.id)
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
            Box(modifier = Modifier.clickable { onFavoriteClick(video) }) {
                Icon(
                    imageVector = favoriteIcon,
                    tint = if (video.isSelected) Color.Red else Color.Gray,
                    modifier = Modifier.clickable {
                        onFavoriteClick(video) // Вызов функции добавления в избранное
                    },
                    contentDescription = null
                )
            }
        }
        HorizontalDivider(color = Color.Gray)
    }
}

@Composable
fun StatisticVideo(modifier: Modifier = Modifier, likes: Int) {
    Box(
        modifier = modifier
            .padding(horizontal = 6.dp)
            .width(140.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF272727)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ThumbUpOffAlt,
                contentDescription = "Likes",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = formatValue(likes),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            VerticalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray
            )
            Icon(
                painter = painterResource(id = R.drawable.thumbs_down),
                tint = Color.White,
                contentDescription = "Dislikes",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = { CircularProgressIndicator() }
    )
}

@SuppressLint("DefaultLocale")
fun formatValue(value: Any): String {
    return when (value) {
        is Int -> {
            when {
                value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
                value >= 1_000 -> String.format("%.1fK", value / 1_000.0)
                else -> value.toString()
            }
        }

        is Duration -> {
            val totalSeconds = value.inWholeSeconds
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val remainingSeconds = totalSeconds % 60

            if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
            } else {
                String.format("%d:%02d", minutes, remainingSeconds)
            }
        }

        else -> throw IllegalArgumentException("Unsupported type")
    }
}

fun Int.toDuration(): Duration {
    return this.toDuration(DurationUnit.SECONDS)
}


// @Composable
//fun VideoScreen(
//    viewModel: VideoViewModel,
//    navController: NavHostController,
//    modifier: Modifier = Modifier
//) {
//    var searchQuery by remember { mutableStateOf("") }
//    var isSearchVisible by remember { mutableStateOf(false) }
//    var searchHistory by remember { mutableStateOf(listOf<String>()) }
//
//    Column(modifier = modifier.background(Color(0xFF0f0f0f))) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = rememberAsyncImagePainter(model = R.drawable.youtube),
//                modifier = Modifier.size(26.dp),
//                contentDescription = null
//            )
//            Text(
//                text = "YouTube",
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//                fontSize = 18.sp
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            // Иконки для кастинга и уведомлений
//            Icon(
//                painter = painterResource(id = R.drawable.baseline_cast_24),
//                modifier = Modifier.size(24.dp),
//                tint = Color.White,
//                contentDescription = null
//            )
//            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
//            Icon(
//                painter = painterResource(id = R.drawable.bell),
//                modifier = Modifier.size(24.dp),
//                tint = Color.White,
//                contentDescription = null
//            )
//            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
//            Box(modifier = Modifier.clickable {
//                isSearchVisible = !isSearchVisible
//            }) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.White,
//                    contentDescription = null
//                )
//            }
//        }
//
//        // Анимированное текстовое поле для поиска
//        AnimatedVisibility(visible = isSearchVisible) {
//            Column {
//                TextField(
//                    value = searchQuery,
//                    onValueChange = { searchQuery = it },
//                    modifier = Modifier.fillMaxWidth(),
//                    placeholder = { Text("Search videos...") },
//                    trailingIcon = {
//                        IconButton(onClick = {
//                            if (searchQuery.isNotBlank()) {
//                                searchHistory = searchHistory + searchQuery
//                                viewModel.handleIntent(VideoIntent.SearchVideos(searchQuery))
//                            }
//                        }) {
//                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
//                        }
//                    }
//                )
//                // История поиска
//                LazyColumn {
//                    items(searchHistory) { historyItem ->
//                        Text(
//                            text = historyItem,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable {
//                                    searchQuery = historyItem
//                                    viewModel.handleIntent(VideoIntent.SearchVideos(historyItem))
//                                }
//                                .padding(8.dp)
//                        )
//                    }
//                }
//            }
//        }
//
//        when (val state = viewModel.state.collectAsState().value) {
//            is VideoState.Idle -> {
//                viewModel.handleIntent(VideoIntent.LoadVideos)
//            }
//
//            is VideoState.Loading -> LoadingScreen()
//            is VideoState.VideosLoaded -> VideoList(state.videos) { videoId ->
//                viewModel.handleIntent(VideoIntent.SelectVideo(videoId))
//                navController.navigate("videoDetails/$videoId")
//            }
//
//            is VideoState.Error -> Text("Error: ${state.error}", modifier = Modifier.padding(20.dp))
//            is VideoState.VideoSelectedWithPoster -> VideoPlayerWithPoster(
//                state.video,
//                state.recommendedVideos,
//                viewModel,
//                navController
//            )
//        }
//    }
//}