package com.techflow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CircuitBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "circuit_anim")

    // Animación de flujo de datos (Fase del punteado)
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "p1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "p2"
    )
    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "p3"
    )

    // Animación de pulso para los nodos
    val nodeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nodes"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0E2030), Color(0xFF0B1622), Color(0xFF081019))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val pathColor = Color(0xFF1D5A6E)
            val nodeColor = Color(0xFF22D3EE)
            val stroke = 1.4.dp.toPx()

            // Ruta 1: Superior Izquierda -> Centro
            val p1 = Path().apply {
                moveTo(w * 0.1f, 0f)
                lineTo(w * 0.1f, h * 0.15f)
                lineTo(w * 0.4f, h * 0.15f)
                lineTo(w * 0.4f, h * 0.45f)
            }
            drawPath(
                path = p1,
                color = pathColor,
                style = Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), phase1)
                )
            )

            // Ruta 2: Derecha -> Abajo
            val p2 = Path().apply {
                moveTo(w, h * 0.1f)
                lineTo(w * 0.7f, h * 0.1f)
                lineTo(w * 0.7f, h * 0.4f)
                lineTo(w * 0.85f, h * 0.4f)
                lineTo(w * 0.85f, h)
            }
            drawPath(
                path = p2,
                color = pathColor,
                style = Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), phase2)
                )
            )

            // Ruta 3: Inferior Izquierda -> Arriba
            val p3 = Path().apply {
                moveTo(0f, h * 0.85f)
                lineTo(w * 0.25f, h * 0.85f)
                lineTo(w * 0.25f, h * 0.6f)
                lineTo(w * 0.55f, h * 0.6f)
                lineTo(w * 0.55f, h)
            }
            drawPath(
                path = p3,
                color = pathColor,
                style = Stroke(
                    width = stroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), phase3)
                )
            )

            // Nodos (Puntos de luz en intersecciones)
            drawCircle(nodeColor, 2.5.dp.toPx(), Offset(w * 0.1f, h * 0.15f), alpha = nodeAlpha)
            drawCircle(nodeColor, 2.5.dp.toPx(), Offset(w * 0.7f, h * 0.4f), alpha = nodeAlpha * 0.8f)
            drawCircle(nodeColor, 2.5.dp.toPx(), Offset(w * 0.25f, h * 0.85f), alpha = nodeAlpha * 0.6f)
            drawCircle(nodeColor, 2.5.dp.toPx(), Offset(w * 0.55f, h * 0.6f), alpha = nodeAlpha)
        }
    }
}
