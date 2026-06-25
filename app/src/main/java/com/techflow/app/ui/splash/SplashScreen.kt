package com.techflow.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techflow.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class SplashRoute(val points: List<Pair<Float, Float>>, val durationMs: Int)

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    val infiniteTransition = rememberInfiniteTransition(label = "splash_bg")

    // 6 rutas de circuito distribuidas: esquinas sup, flancos medios, base
    val routes = remember {
        listOf(
            SplashRoute(listOf(0f to 0f,    0f to 0.13f,  0.22f to 0.13f, 0.22f to 0.30f), 6500),
            SplashRoute(listOf(1f to 0f,    1f to 0.11f,  0.76f to 0.11f, 0.76f to 0.25f), 7800),
            SplashRoute(listOf(0f to 0.44f, 0.16f to 0.44f, 0.16f to 0.60f, 0.06f to 0.60f, 0.06f to 0.75f), 8400),
            SplashRoute(listOf(1f to 0.40f, 0.84f to 0.40f, 0.84f to 0.58f, 0.94f to 0.58f, 0.94f to 0.72f), 7200),
            SplashRoute(listOf(0.08f to 1f, 0.08f to 0.82f, 0.28f to 0.82f, 0.28f to 0.92f), 9500),
            SplashRoute(listOf(0.92f to 1f, 0.92f to 0.85f, 0.72f to 0.85f, 0.60f to 0.85f), 6000),
        )
    }

    val circuitPhases = routes.mapIndexed { index, route ->
        infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(route.durationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "route_$index"
        )
    }

    // 10 nodos cyan en las intersecciones de las rutas
    val nodePositions = remember {
        listOf(
            0.22f to 0.13f, 0.22f to 0.30f,
            0.76f to 0.11f, 0.76f to 0.25f,
            0.16f to 0.44f, 0.16f to 0.60f,
            0.84f to 0.40f, 0.84f to 0.58f,
            0.08f to 0.82f, 0.28f to 0.82f,
        )
    }

    val nodeAlphas = nodePositions.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0.35f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(index * 150)
            ),
            label = "node_$index"
        )
    }

    // Anillos de radar — desfasados 1100ms (efecto onda expansiva)
    val ring1Progress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring1"
    )
    val ring2Progress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
            initialStartOffset = StartOffset(1100)
        ),
        label = "ring2"
    )

    // Barra decorativa: relleno oscila izquierda ↔ derecha
    val loadingProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading"
    )

    // Logo: pop-in con rebote suave
    val logoScale = remember { Animatable(0.7f) }
    val logoAlpha = remember { Animatable(0f) }

    // Texto: fade-in + slide-up retrasados 600ms respecto al logo
    var showText by remember { mutableStateOf(false) }
    val textAlpha by animateFloatAsState(
        targetValue = if (showText) 1f else 0f,
        animationSpec = tween(500),
        label = "textAlpha"
    )
    val textSlide by animateFloatAsState(
        targetValue = if (showText) 0f else 20f,
        animationSpec = tween(500),
        label = "textSlide"
    )

    // 600ms logo + 1200ms espera = 1800ms total antes de navegar
    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(1.08f, animationSpec = tween(600, easing = FastOutSlowInEasing))
            logoScale.animateTo(
                1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        logoAlpha.animateTo(1f, animationSpec = tween(600))
        showText = true
        delay(1200)
        onSplashFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Capa 1: gradiente de fondo + circuito animado ──────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF123B4D), Color(0xFF0B1622), Color(0xFF081019)),
                    center = Offset(size.width * 0.5f, size.height * 0.40f),
                    radius = size.height * 0.75f
                )
            )

            val w = size.width
            val h = size.height
            val pathColor = Color(0xFF1D5A6E).copy(alpha = 0.7f)
            val nodeColor = Color(0xFF22D3EE)
            val strokePx = 1.2.dp.toPx()

            routes.forEachIndexed { i, route ->
                val path = Path().apply {
                    route.points.forEachIndexed { pi, (xf, yf) ->
                        if (pi == 0) moveTo(w * xf, h * yf) else lineTo(w * xf, h * yf)
                    }
                }
                drawPath(
                    path = path,
                    color = pathColor,
                    style = Stroke(
                        width = strokePx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 15f), circuitPhases[i].value)
                    )
                )
            }

            nodePositions.forEachIndexed { i, (xf, yf) ->
                drawCircle(
                    color = nodeColor,
                    radius = 2.dp.toPx(),
                    center = Offset(w * xf, h * yf),
                    alpha = nodeAlphas[i].value
                )
            }
        }

        // ── Capa 2: contenido central ──────────────────────────────────────────────
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Box de 250dp: anillos de radar (Canvas) + logo encima
            Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = size.minDimension * 0.28f // ~70dp base

                    val r1 = baseRadius * (0.9f + ring1Progress * 0.8f)
                    drawCircle(
                        color = Color(0xFF22D3EE), radius = r1, center = center,
                        alpha = (1f - ring1Progress).coerceIn(0f, 1f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    val r2 = baseRadius * (0.9f + ring2Progress * 0.8f)
                    drawCircle(
                        color = Color(0xFF22D3EE), radius = r2, center = center,
                        alpha = (1f - ring2Progress).coerceIn(0f, 1f),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.register),
                    contentDescription = "TechFlow logo",
                    modifier = Modifier
                        .size(90.dp)
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TechFlow",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha).offset(y = textSlide.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Gestión de Inventario Tecnológico",
                fontSize = 12.sp,
                color = Color(0xFFB0C4D0),
                modifier = Modifier.alpha(textAlpha).offset(y = textSlide.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Barra de carga: 36dp de relleno que se mueve de izq a der en loop
            Box(modifier = Modifier.width(160.dp).height(3.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        color = Color(0xFF22D3EE).copy(alpha = 0.25f),
                        size = size,
                        cornerRadius = CornerRadius(1.5.dp.toPx())
                    )
                    val fillWidth = 36.dp.toPx()
                    val maxOff = (size.width - fillWidth).coerceAtLeast(0f)
                    drawRoundRect(
                        color = Color(0xFF22D3EE).copy(alpha = 0.85f),
                        topLeft = Offset(maxOff * loadingProgress, 0f),
                        size = Size(fillWidth, size.height),
                        cornerRadius = CornerRadius(1.5.dp.toPx())
                    )
                }
            }
        }
    }
}
