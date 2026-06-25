package com.techflow.app.ui.statistics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.R
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.ui.components.StockBadge
import com.techflow.app.ui.components.getCategoryColor
import com.techflow.app.ui.theme.ErrorRed
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.ui.theme.SuccessGreen
import com.techflow.app.viewmodel.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val bgRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Scaffold(
        containerColor = Color(0xFF020617),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Estadística 📊",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.nuevoproducto),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )
            Canvas(modifier = Modifier.fillMaxSize().rotate(bgRotation)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryBlue.copy(alpha = 0.1f), Color.Transparent),
                        center = center
                    ),
                    radius = size.maxDimension / 1.5f
                )
            }

            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.totalProducts == 0) {
                EmptyStatisticsState(modifier = Modifier.padding(paddingValues))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        StaggeredVisibility(index = 0) {
                            AnimatedCategoryChart(
                                categoryData = uiState.categoryDistribution,
                                totalItems = uiState.totalProducts
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "Resumen Rápido ⚡",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MetricCard3D(
                                    modifier = Modifier.weight(1f),
                                    title = "Inventario 📦",
                                    value = "${uiState.totalProducts}",
                                    accentColor = PrimaryBlue
                                )
                                MetricCard3D(
                                    modifier = Modifier.weight(1f),
                                    title = "Stock Bajo ⚠️",
                                    value = "${uiState.lowStockCount}",
                                    accentColor = if (uiState.lowStockCount > 0) Color(0xFFFF3D00) else SuccessGreen
                                )
                            }
                        }
                    }

                    item {
                        StaggeredVisibility(index = 2) {
                            GradientStatCard(
                                title = "Valoración de Activos 💰",
                                value = "S/. ${"%.2f".format(uiState.totalInventoryValue)}",
                                subtitle = "Capital total invertido en tecnología",
                                icon = Icons.Default.AccountBalanceWallet
                            )
                        }
                    }

                    if (uiState.lowStockProducts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Alertas Críticas 🚨",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        itemsIndexed(uiState.lowStockProducts) { index, product ->
                            StaggeredVisibility(index = index + 3) {
                                GlassAlertCard(product, onProductClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AnimatedCategoryChart(
    categoryData: Map<String, Int>,
    totalItems: Int
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val curSweepAngle by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "sweep"
    )
    LaunchedEffect(Unit) { animationPlayed = true }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Distribución por Categoría 📂",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    var startAngle = -90f
                    categoryData.forEach { (category, count) ->
                        val sweepAngle = (count.toFloat() / totalItems) * 360f * curSweepAngle
                        drawArc(
                            color = getCategoryColor(category),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 35f, cap = StrokeCap.Round)
                        )
                        startAngle += (count.toFloat() / totalItems) * 360f
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$totalItems", fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text("ITEMS TOTALES", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryData.forEach { (category, _) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(getCategoryColor(category), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(category, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard3D(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(accentColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(title.takeLast(2))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(title.dropLast(2).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun GradientStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.2f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(SuccessGreen.copy(alpha = 0.05f), Color.Transparent)
                        )
                    )
            )
            
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, null, tint = SuccessGreen, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(title, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(subtitle, fontSize = 11.sp, color = SuccessGreen.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun GlassAlertCard(product: com.techflow.app.domain.model.Product, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(product.id) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF450a0a).copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF991b1b).copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).background(Color(0xFF991b1b).copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("💥")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.nombre, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Stock Insuficiente", color = Color(0xFFf87171), fontSize = 12.sp)
            }
            StockBadge(product.cantidad, product.stockMinimo)
        }
    }
}

@Composable
private fun EmptyStatisticsState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔭", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Sin datos aún", color = Color.White, fontWeight = FontWeight.Bold)
            Text("Agrega productos para ver la magia ✨", color = Color.White.copy(alpha = 0.5f))
        }
    }
}
