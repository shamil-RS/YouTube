package com.example.youtube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.youtube.navigation.AppScreen
import com.example.youtube.ui.theme.YouTubeTheme
import com.example.youtube.ui.theme.screen.homescreen.VideoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: VideoViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeTheme {
                AppScreen(viewModel = viewModel)
            }
        }
    }
}
