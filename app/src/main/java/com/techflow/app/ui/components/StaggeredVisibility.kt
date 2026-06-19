package com.techflow.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

// StaggeredVisibility - envuelve un item con una animación de entrada (fadeIn + slideInVertically)
// que se retrasa según su posición en la lista, creando un efecto escalonado
@Composable
fun StaggeredVisibility(
    index: Int,
    delayMs: Long = 60L,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * delayMs)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(350)) +
            slideInVertically(initialOffsetY = { it / 4 }, animationSpec = tween(350))
    ) {
        content()
    }
}
