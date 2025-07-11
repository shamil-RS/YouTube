package com.example.youtube.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import com.example.youtube.R

@Composable
fun YouTubeBottomBar(
    navBackStack: NavBackStack,
    modifier: Modifier = Modifier,
) {
    val currentScreen = navBackStack.last()

    BottomAppBar(
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth(),
        tonalElevation = 10.dp,
        containerColor = Color.Black,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = CenterVertically
        ) {
            bottomNavigationBarItems.forEach { screen ->
                BottomBarItem(
                    label = screen.title,
                    icon = screen.icon,
                    selected = screen == currentScreen,
                    onClick = { navBackStack.add(screen) }
                )
            }
        }
    }
}

@Composable
fun BottomBarItem(
    @DrawableRes icon: Int = R.drawable.baseline_home_24,
    label: String = "Главная",
    onClick: () -> Unit = {},
    selected: Boolean = false,
) {

    val colorT = if (selected) Color.White else Color(0xFF767e85)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick() }),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(
            modifier = Modifier
                .align(CenterHorizontally)
                .size(22.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = colorT
        )
        Text(text = label, color = colorT, fontWeight = FontWeight.Bold)
    }
}