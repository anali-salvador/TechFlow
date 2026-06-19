package com.techflow.app.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.ui.components.StockBadge
import com.techflow.app.ui.components.getCategoryColor
import com.techflow.app.ui.theme.ErrorRed
import com.techflow.app.ui.theme.OnSurfaceLight
import com.techflow.app.ui.theme.SuccessGreen
import com.techflow.app.ui.theme.SurfaceLight
import com.techflow.app.viewmodel.StatisticsViewModel

// StatisticsScreen - Pantalla 6 del expediente técnico (Estadísticas)
// Tarjeta con total de productos registrados
// Tarjeta con cantidad de productos con stock bajo (resaltados en rojo)
// Tarjeta con valor total estimado del inventario (suma de precio x cantidad)
// Lista de productos en riesgo de stock con acceso rápido a su detalle
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Estadísticas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.totalProducts == 0 -> {
                EmptyStatisticsState(modifier = Modifier.padding(paddingValues))
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta: Total de productos registrados
                    item {
                        StaggeredVisibility(index = 0) {
                            StatCard(
                                icon = Icons.Default.ShoppingCart,
                                iconColor = MaterialTheme.colorScheme.primary,
                                title = "Total de productos",
                                value = "${uiState.totalProducts}",
                                subtitle = "productos registrados en tu inventario"
                            )
                        }
                    }

                    // Tarjeta: Productos con stock bajo (resaltado en rojo)
                    item {
                        StaggeredVisibility(index = 1) {
                            StatCard(
                                icon = Icons.Default.Warning,
                                iconColor = if (uiState.lowStockCount > 0) ErrorRed else SuccessGreen,
                                title = "Productos con stock bajo",
                                value = "${uiState.lowStockCount}",
                                subtitle = "productos con cantidad igual o menor al stock mínimo"
                            )
                        }
                    }

                    // Tarjeta: Valor total del inventario
                    item {
                        StaggeredVisibility(index = 2) {
                            StatCard(
                                icon = Icons.Default.Star,
                                iconColor = SuccessGreen,
                                title = "Valor total del inventario",
                                value = "S/. ${"%.2f".format(uiState.totalInventoryValue)}",
                                subtitle = "suma de precio × cantidad de cada producto"
                            )
                        }
                    }

                    // Gráfica Circular: Distribución por categorías
                    if (uiState.categoryDistribution.isNotEmpty()) {
                        item {
                            StaggeredVisibility(index = 3) {
                                CategoryDonutChart(
                                    categoryData = uiState.categoryDistribution,
                                    totalItems = uiState.totalProducts
                                )
                            }
                        }
                    }

                    // Sección: Lista de productos en riesgo de stock
                    if (uiState.lowStockProducts.isNotEmpty()) {
                        item {
                            Text(
                                text = "Productos en riesgo de stock",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Lista de productos con stock bajo con acceso rápido al detalle
                        itemsIndexed(uiState.lowStockProducts) { index, product ->
                            StaggeredVisibility(index = index + 4) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onProductClick(product.id) },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            // Ícono circular con la inicial de la categoría
                                            val categoryColor = getCategoryColor(product.categoria)
                                            Surface(
                                                modifier = Modifier.size(40.dp),
                                                shape = CircleShape,
                                                color = categoryColor.copy(alpha = 0.18f)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = product.categoria.take(1).uppercase(),
                                                        fontWeight = FontWeight.Bold,
                                                        color = categoryColor
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = product.nombre,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${product.categoria} · ${product.marca}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        StockBadge(
                                            cantidad = product.cantidad,
                                            stockMinimo = product.stockMinimo
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Composable para la gráfica circular (Donut Chart) de categorías
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryDonutChart(
    categoryData: Map<String, Int>,
    totalItems: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Distribución por Categorías",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceLight,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    var startAngle = -90f
                    categoryData.forEach { (category, count) ->
                        val sweepAngle = (count.toFloat() / totalItems) * 360f
                        drawArc(
                            color = getCategoryColor(category),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(width = 35f, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalItems",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Leyenda de la gráfica
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 3
            ) {
                categoryData.forEach { (category, count) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(12.dp),
                            shape = CircleShape,
                            color = getCategoryColor(category)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$category ($count)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// Composable reutilizable para las tarjetas de estadísticas
// Ícono circular de color a la izquierda, fondo blanco con sombra, bordes redondeados de 20dp
@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono circular de color vibrante a la izquierda
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.18f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = OnSurfaceLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Estado vacío ilustrativo cuando no hay productos registrados todavía
@Composable
private fun EmptyStatisticsState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Aún no hay estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega productos a tu inventario\npara ver tus estadísticas aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
