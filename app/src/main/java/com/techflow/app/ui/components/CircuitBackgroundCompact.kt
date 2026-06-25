package com.techflow.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

private data class CompactCircuitRoute(val points: List<Pair<Float, Float>>, val durationMs: Int)

@Composable
fun CircuitBackgroundCompact(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "circuit_compact_anim")

    // Rutas con ángulos rectos distribuidas para llenar el fondo
    val routes = remember {
        listOf(
            CompactCircuitRoute(listOf(0f to 0f, 0f to 0.15f, 0.18f to 0.15f, 0.18f to 0.32f), 6000),
            CompactCircuitRoute(listOf(0.38f to 0f, 0.38f to 0.10f, 0.58f to 0.10f, 0.58f to 0.26f), 7200),
            CompactCircuitRoute(listOf(1f to 0f, 1f to 0.08f, 0.80f to 0.08f, 0.80f to 0.24f), 6600),
            CompactCircuitRoute(listOf(0f to 0.38f, 0.22f to 0.38f, 0.22f to 0.56f, 0.44f to 0.56f, 0.44f to 0.76f), 8400),
            CompactCircuitRoute(listOf(1f to 0.42f, 0.76f to 0.42f, 0.76f to 0.62f, 0.90f to 0.62f, 0.90f to 1f), 7800),
            CompactCircuitRoute(listOf(0f to 0.72f, 0.14f to 0.72f, 0.14f to 0.86f, 0.32f to 0.86f), 9000),
            CompactCircuitRoute(listOf(0.62f to 1f, 0.62f to 0.80f, 0.46f to 0.80f, 0.46f to 0.64f), 8000),
            // Nuevas rutas para llenar espacios
            CompactCircuitRoute(listOf(0.15f to 1f, 0.15f to 0.90f, 0.40f to 0.90f, 0.40f to 1f), 7500),
            CompactCircuitRoute(listOf(0.85f to 1f, 0.85f to 0.75f, 0.70f to 0.75f, 0.70f to 0.85f), 8200),
            CompactCircuitRoute(listOf(0.50f to 0f, 0.50f to 0.20f, 0.30f to 0.20f, 0.30f to 0.45f), 9500),
            CompactCircuitRoute(listOf(0f to 0.55f, 0.10f to 0.55f, 0.10f to 0.65f, 0.25f to 0.65f), 6800),
            CompactCircuitRoute(listOf(1f to 0.20f, 0.92f to 0.20f, 0.92f to 0.40f, 0.82f to 0.40f), 7100),
            CompactCircuitRoute(listOf(0.35f to 0.60f, 0.55f to 0.60f, 0.55f to 0.50f, 0.70f to 0.50f), 8800),
        )
    }

    val phases = routes.map { route ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(route.durationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "compact_phase_${route.durationMs}"
        )
    }

    // Nodos en intersecciones y puntos clave
    val nodePositions = remember {
        listOf(
            0.18f to 0.15f, 0.18f to 0.32f,
            0.38f to 0.10f, 0.58f to 0.10f, 0.58f to 0.26f,
            0.80f to 0.08f, 0.80f to 0.24f,
            0.22f to 0.38f, 0.22f to 0.56f, 0.44f to 0.56f,
            0.76f to 0.42f, 0.76f to 0.62f,
            0.14f to 0.72f, 0.14f to 0.86f, 0.32f to 0.86f,
            0.62f to 0.80f,
            // Nuevos nodos
            0.15f to 0.90f, 0.40f to 0.90f,
            0.85f to 0.75f, 0.70f to 0.75f,
            0.50f to 0.20f, 0.30f to 0.20f,
            0.10f to 0.55f, 0.10f to 0.65f,
            0.92f to 0.40f, 0.55f to 0.60f,
            0.55f to 0.50f
        )
    }

    val nodeAlphas = nodePositions.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(index * 120)
            ),
            label = "compact_node_$index"
        )
    }

    val fillerLines = remember {
        listOf(
            Triple(0.68f, 0.05f, 0.35f),
            Triple(0.28f, 0.60f, 0.95f),
            Triple(0.50f, 0.38f, 0.72f),
            Triple(0.12f, 0.10f, 0.40f),
            Triple(0.88f, 0.50f, 0.90f),
            Triple(0.45f, 0.80f, 1f),
            Triple(0.20f, 0f, 0.30f),
            Triple(0.75f, 0.15f, 0.45f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B1622))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val pathColor = Color(0xFF1D5A6E).copy(alpha = 0.7f)
            val nodeColor = Color(0xFF22D3EE)
            val fillerColor = Color(0xFF11313D).copy(alpha = 0.5f)
            val stroke = 1.2.dp.toPx()

            routes.forEachIndexed { index, route ->
                val path = Path().apply {
                    route.points.forEachIndexed { pointIndex, (xFrac, yFrac) ->
                        if (pointIndex == 0) moveTo(w * xFrac, h * yFrac)
                        else lineTo(w * xFrac, h * yFrac)
                    }
                }
                drawPath(
                    path = path,
                    color = pathColor,
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), phases[index].value)
                    )
                )
            }

            fillerLines.forEach { (xFrac, yStartFrac, yEndFrac) ->
                drawLine(
                    color = fillerColor,
                    start = Offset(w * xFrac, h * yStartFrac),
                    end = Offset(w * xFrac, h * yEndFrac),
                    strokeWidth = 0.8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 12f), 0f)
                )
            }

            nodePositions.forEachIndexed { index, (xFrac, yFrac) ->
                drawCircle(
                    color = nodeColor,
                    radius = 2.dp.toPx(),
                    center = Offset(w * xFrac, h * yFrac),
                    alpha = nodeAlphas[index].value
                )
            }
        }
    }
}
