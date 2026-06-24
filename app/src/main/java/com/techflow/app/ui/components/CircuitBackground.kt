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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// Definición de una ruta de circuito: lista de puntos (fracción de w, fracción de h) y duración de su flujo animado
private data class CircuitRoute(val points: List<Pair<Float, Float>>, val durationMs: Int)

@Composable
fun CircuitBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "circuit_anim")

    // 9 rutas tipo circuito (ángulos rectos) distribuidas en toda la altura: arriba, medio y abajo
    val routes = remember {
        listOf(
            // Zona superior
            CircuitRoute(listOf(0.08f to 0f, 0.08f to 0.12f, 0.30f to 0.12f, 0.30f to 0.30f), 6200),
            CircuitRoute(listOf(0.45f to 0f, 0.45f to 0.10f, 0.60f to 0.10f, 0.60f to 0.22f), 7000),
            CircuitRoute(listOf(0.95f to 0f, 0.95f to 0.06f, 0.78f to 0.06f, 0.78f to 0.18f), 6400),
            // Ruta larga que cruza de arriba hacia abajo
            CircuitRoute(listOf(1f to 0.08f, 0.72f to 0.08f, 0.72f to 0.28f, 0.88f to 0.28f, 0.88f to 1f), 9400),
            // Zona media
            CircuitRoute(listOf(0f to 0.45f, 0.15f to 0.45f, 0.15f to 0.55f, 0.35f to 0.55f, 0.35f to 0.40f), 6800),
            CircuitRoute(listOf(1f to 0.50f, 0.80f to 0.50f, 0.80f to 0.35f, 0.92f to 0.35f), 9800),
            // Zona inferior
            CircuitRoute(listOf(0f to 0.88f, 0.22f to 0.88f, 0.22f to 0.62f, 0.50f to 0.62f, 0.50f to 1f), 8200),
            CircuitRoute(listOf(1f to 0.92f, 0.65f to 0.92f, 0.65f to 0.75f, 0.40f to 0.75f), 7600),
            CircuitRoute(listOf(0.30f to 1f, 0.30f to 0.85f, 0.10f to 0.85f, 0.10f to 0.70f), 10000)
        )
    }

    // Fase de flujo (dasharray animado) por ruta, cada una con su propia duración
    val phases = routes.map { route ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(route.durationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "phase_${route.durationMs}"
        )
    }

    // 20 nodos en intersecciones de las rutas, repartidos por toda la pantalla
    val nodePositions = remember {
        listOf(
            0.08f to 0.12f, 0.30f to 0.12f,
            0.45f to 0.10f, 0.60f to 0.10f,
            0.95f to 0.06f, 0.78f to 0.06f,
            0.72f to 0.08f, 0.72f to 0.28f, 0.88f to 0.28f,
            0.15f to 0.45f, 0.15f to 0.55f, 0.35f to 0.55f,
            0.80f to 0.50f, 0.80f to 0.35f,
            0.22f to 0.88f, 0.22f to 0.62f, 0.50f to 0.62f,
            0.65f to 0.92f, 0.65f to 0.75f,
            0.30f to 0.85f
        )
    }

    // Delay distinto por nodo para que el pulso no se vea sincronizado
    val nodeAlphas = nodePositions.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(index * 180)
            ),
            label = "node_$index"
        )
    }

    // Líneas verticales cortas de relleno (sin nodos, más sutiles, color más oscuro)
    val fillerLines = remember {
        listOf(
            Triple(0.18f, 0.05f, 0.28f),
            Triple(0.62f, 0.55f, 0.80f),
            Triple(0.40f, 0.78f, 0.95f)
        )
    }

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
            val fillerColor = Color(0xFF11313D)
            val stroke = 1.4.dp.toPx()

            // Rutas principales del circuito
            routes.forEachIndexed { index, route ->
                val path = Path().apply {
                    route.points.forEachIndexed { pointIndex, (xFrac, yFrac) ->
                        if (pointIndex == 0) moveTo(w * xFrac, h * yFrac) else lineTo(w * xFrac, h * yFrac)
                    }
                }
                drawPath(
                    path = path,
                    color = pathColor,
                    style = Stroke(
                        width = stroke,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), phases[index].value)
                    )
                )
            }

            // Líneas verticales de relleno, estáticas y más sutiles
            fillerLines.forEach { (xFrac, yStartFrac, yEndFrac) ->
                drawLine(
                    color = fillerColor,
                    start = Offset(w * xFrac, h * yStartFrac),
                    end = Offset(w * xFrac, h * yEndFrac),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 10f), 0f)
                )
            }

            // Nodos (puntos de luz en intersecciones), cada uno con su propio pulso
            nodePositions.forEachIndexed { index, (xFrac, yFrac) ->
                drawCircle(
                    color = nodeColor,
                    radius = 2.5.dp.toPx(),
                    center = Offset(w * xFrac, h * yFrac),
                    alpha = nodeAlphas[index].value
                )
            }
        }
    }
}
