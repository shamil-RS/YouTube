package com.example.youtube.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.youtube.ui.theme.screen.detailscreen.VideoDetailsScreen
import com.example.youtube.ui.theme.screen.homescreen.VideoScreen
import com.example.youtube.ui.theme.screen.homescreen.VideoViewModel
import com.example.youtube.ui.theme.screen.userscreen.UserScreen

@Composable
fun NavStack(
    modifier: Modifier = Modifier,
    navBackStack: NavBackStack,
    viewModel: VideoViewModel
) {
    NavDisplay(
        backStack = navBackStack,
        onBack = { navBackStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeBottomBarNavigation.HomeScreen> {
                VideoScreen(viewModel, navBackStack)
            }

            entry<VideoDetails> {
                VideoDetailsScreen(viewModel, it.videoId, navBackStack)
            }

            entry<HomeBottomBarNavigation.UserScreen> {
                UserScreen(viewModel, navBackStack = navBackStack)
            }
        }
    )
}