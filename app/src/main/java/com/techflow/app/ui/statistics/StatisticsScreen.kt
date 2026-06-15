package com.techflow.app.ui.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StockBadge
import com.techflow.app.viewmodel.StatisticsViewModel

// StatisticsScreen - Pantalla 6 del expediente técnico (Estadísticas)
// Tarjeta con total de productos registrados
// Tarjeta con cantidad de productos con stock bajo (resaltados en rojo)
// Tarjeta con valor total estimado del inventario (suma de precio x cantidad)
// Lista de productos en riesgo de stock con acceso rápido a su detalle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta: Total de productos registrados
                item {
                    StatCard(
                        title = "Total de productos",
                        value = "${uiState.totalProducts}",
                        subtitle = "productos registrados en tu inventario",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Tarjeta: Productos con stock bajo (resaltado en rojo)
                item {
                    StatCard(
                        title = "Productos con stock bajo",
                        value = "${uiState.lowStockCount}",
                        subtitle = "productos con cantidad igual o menor al stock mínimo",
                        containerColor = if (uiState.lowStockCount > 0)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (uiState.lowStockCount > 0)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Tarjeta: Valor total del inventario
                item {
                    StatCard(
                        title = "Valor total del inventario",
                        value = "S/. ${"%.2f".format(uiState.totalInventoryValue)}",
                        subtitle = "suma de precio × cantidad de cada producto",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
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
                    items(
                        items = uiState.lowStockProducts,
                        key = { it.id }
                    ) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProductClick(product.id) },
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
                                Column(modifier = Modifier.weight(1f)) {
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

// Composable reutilizable para las tarjetas de estadísticas
@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
