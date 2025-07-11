package com.example.youtube.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.youtube.navigation.HomeBottomBarNavigation.*
import com.example.youtube.ui.theme.screen.homescreen.VideoViewModel

@Composable
fun AppScreen(viewModel: VideoViewModel) {
    val navBackStack = rememberNavBackStack(HomeScreen)
    val lastScreen = navBackStack.lastOrNull()
    val isVisibleBottomBar = lastScreen is HomeScreen || lastScreen is UserScreen

    Scaffold(
        bottomBar = {
            if (isVisibleBottomBar) {
                YouTubeBottomBar(
                    navBackStack = navBackStack,
                    modifier = Modifier,
                )
            }
        },
    ) { innerPadding ->
        NavStack(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            navBackStack = navBackStack,
            viewModel = viewModel
        )
    }
}