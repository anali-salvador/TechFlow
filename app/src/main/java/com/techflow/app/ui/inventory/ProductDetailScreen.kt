package com.techflow.app.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.components.ConfirmDeleteDialog
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StockBadge
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
    val product = uiState.selectedProduct

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
            TopAppBar(
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (product == null) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nombre del producto grande y en negrita
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Badge de stock
                StockBadge(
                    cantidad = product.cantidad,
                    stockMinimo = product.stockMinimo
                )

                // Alerta si el stock está bajo
                if (product.cantidad <= product.stockMinimo) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "⚠ Stock bajo: quedan ${product.cantidad} unidades (mínimo: ${product.stockMinimo})",
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                HorizontalDivider()

                // Campos del producto organizados en filas
                DetailRow(label = "Categoría", value = product.categoria)
                DetailRow(label = "Marca", value = product.marca)
                DetailRow(label = "Precio", value = "S/. ${"%.2f".format(product.precio)}")
                DetailRow(label = "Cantidad en stock", value = "${product.cantidad} unidades")
                DetailRow(label = "Stock mínimo", value = "${product.stockMinimo} unidades")

                if (!product.descripcion.isNullOrBlank()) {
                    HorizontalDivider()
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.descripcion,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// Composable auxiliar para mostrar una fila de detalle (etiqueta: valor)
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
