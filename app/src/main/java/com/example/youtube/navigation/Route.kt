package com.example.youtube.navigation

import androidx.navigation3.runtime.NavKey
import com.example.youtube.R
import kotlinx.serialization.Serializable

@Serializable
data class VideoDetails(val videoId: String) : NavKey

@Serializable
sealed class HomeBottomBarNavigation(
    val title: String,
    val icon: Int
) : NavKey {

    @Serializable
    data object HomeScreen : HomeBottomBarNavigation(
        title = "Главная",
        icon = R.drawable.baseline_home_24
    )

    @Serializable
    data object UserScreen : HomeBottomBarNavigation(
        title = "Профиль",
        icon = R.drawable.baseline_supervised_user_circle_24,
    )
}

val bottomNavigationBarItems = listOf(
    HomeBottomBarNavigation.HomeScreen,
    HomeBottomBarNavigation.UserScreen
)