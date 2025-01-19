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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    icons: List<Pair<Int, String>>,
    currentScreen: String,
    onIconClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = 30.dp)
            .height(50.dp)
    ) {
        // Background of the navbar
        Image(
            painter = painterResource(id = R.drawable.navbar_bottom),
            contentDescription = "Navbar",
            modifier = Modifier
                .fillMaxSize()

        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icons.forEachIndexed { index, (iconRes, screenName) ->
                if (index > 0) {
                    Spacer(modifier = Modifier.width(50.dp))
                }

                val isSelected = currentScreen == screenName
                val iconAlpha = if (isSelected) 1f else 0.6f
                val iconSize = if (isSelected) 35.dp else 30.dp

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



