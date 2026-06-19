package com.techflow.app.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.domain.model.Product
import com.techflow.app.ui.components.ConfirmDeleteDialog
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.ui.components.StockBadge
import com.techflow.app.ui.components.getCategoryColor
import com.techflow.app.ui.theme.LowStockOrange
import com.techflow.app.viewmodel.InventoryViewModel

// ProductDetailScreen - Pantalla 3 del expediente técnico
// Muestra todos los campos del producto: nombre, categoría, marca, precio, cantidad, stockMinimo, descripción
// Tiene botón Editar que navega al formulario y botón Eliminar con diálogo de confirmación
// Indicador visual si el stock está por debajo del mínimo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: InventoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    // Carga el producto al entrar a la pantalla
    // LaunchedEffect se ejecuta una sola vez cuando productId cambia
    LaunchedEffect(productId) {
        viewModel.loadProductById(productId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val product: Product? = uiState.selectedProduct

    // Estado para controlar si se muestra el diálogo de eliminar
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog && product != null) {
        ConfirmDeleteDialog(
            productName = product.nombre,
            onConfirm = {
                viewModel.deleteProduct(product)
                showDeleteDialog = false
                onBackClick() // Vuelve a la lista después de eliminar
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del Producto") },
                // Flecha para volver a la lista
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                // Botones de editar y eliminar en la barra superior
                actions = {
                    if (product != null) {
                        IconButton(onClick = { onEditClick(product.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (product == null) {
            LoadingIndicator()
        } else {
            val categoryColor = getCategoryColor(product.categoria)
            val isLowStock = product.cantidad <= product.stockMinimo

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Cabecera grande con ícono de la categoría, nombre del producto y badge de stock
                StaggeredVisibility(index = 0, delayMs = 80L) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = categoryColor.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = getCategoryIcon(product.categoria),
                                    contentDescription = product.categoria,
                                    tint = categoryColor,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = product.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StockBadge(
                            cantidad = product.cantidad,
                            stockMinimo = product.stockMinimo
                        )
                    }
                }

                // Alerta visual si el stock está bajo
                if (isLowStock) {
                    StaggeredVisibility(index = 1, delayMs = 80L) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = LowStockOrange.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = LowStockOrange,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Stock bajo",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = LowStockOrange
                                    )
                                    Text(
                                        text = "Quedan ${product.cantidad} unidades (mínimo: ${product.stockMinimo})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = LowStockOrange
                                    )
                                }
                            }
                        }
                    }
                }

                // Campos del producto en Cards individuales con ícono según el campo
                StaggeredVisibility(index = 2, delayMs = 80L) {
                    DetailFieldCard(icon = Icons.Default.Info, label = "Categoría", value = product.categoria)
                }
                StaggeredVisibility(index = 3, delayMs = 80L) {
                    DetailFieldCard(icon = Icons.Default.Info, label = "Marca", value = product.marca)
                }
                StaggeredVisibility(index = 4, delayMs = 80L) {
                    DetailFieldCard(icon = Icons.Default.Info, label = "Precio", value = "S/. ${"%.2f".format(product.precio)}")
                }
                StaggeredVisibility(index = 5, delayMs = 80L) {
                    DetailFieldCard(icon = Icons.Default.Info, label = "Cantidad en stock", value = "${product.cantidad} unidades")
                }
                StaggeredVisibility(index = 6, delayMs = 80L) {
                    DetailFieldCard(icon = Icons.Default.Warning, label = "Stock mínimo", value = "${product.stockMinimo} unidades")
                }

                if (!product.descripcion.isNullOrBlank()) {
                    StaggeredVisibility(index = 7, delayMs = 80L) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Descripción",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = product.descripcion,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Ícono representativo según la categoría del producto
// Computer, Mouse, Headphones, Memory, Tablet, Monitor y Devices no existen en Icons.Default
// (solo en material-icons-extended), por lo que se usan alternativas disponibles en el set core
private fun getCategoryIcon(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "laptop" -> Icons.Default.Build
        "celular" -> Icons.Default.Phone
        "periférico" -> Icons.Default.Settings
        "accesorio" -> Icons.Default.Star
        "componente" -> Icons.AutoMirrored.Filled.List
        "tablet" -> Icons.Default.AccountBox
        "monitor" -> Icons.Default.PlayArrow
        else -> Icons.Default.ShoppingCart
    }
}

// Card individual para mostrar un campo del producto con ícono, etiqueta y valor
@Composable
private fun DetailFieldCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
