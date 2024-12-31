package com.example.shakeit.ui.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shakeit.R

@Composable
fun NavBar(
    icons: List<Pair<Int, String>>, // Lista di icone e relativi screenName
    currentScreen: String, // Schermata attuale
    onIconClick: (String) -> Unit, // Azione al clic
    onLogout: () -> Unit, // Azione al clic sulla freccia indietro
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        // Sfondo della navbar
        Image(
            painter = painterResource(id = R.drawable.navbar_bottom),
            contentDescription = "Navbar",
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.forEach { (iconRes, screenName) ->
                val isSelected = currentScreen == screenName
                val iconAlpha = if (isSelected) 1f else 0.6f
                val iconSize = if (isSelected) 26.dp else 22.dp

                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "$screenName Icon",
                    modifier = Modifier
                        .size(iconSize)
                        .clickable {
                            if (screenName == "back") {
                                onLogout()
                            } else {
                                onIconClick(screenName)
                            }
                        }
                        .alpha(iconAlpha)
                )
            }
        }
    }
}



