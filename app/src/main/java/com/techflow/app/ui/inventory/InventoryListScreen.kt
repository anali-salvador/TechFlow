package com.techflow.app.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.components.ErrorMessage
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.ProductCard
import com.techflow.app.viewmodel.InventoryViewModel

// InventoryListScreen - Pantalla 2 del expediente técnico (Pantalla Principal)
// Lista de productos con LazyColumn, botón flotante para agregar
// Menú superior con opciones de Explorar, Estadísticas y Cerrar sesión
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onExploreClick: () -> Unit,
    onStatisticsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado para controlar el menú desplegable
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TechFlow - Inventario") },
                actions = {
                    // Botón de buscar que lleva a la pantalla Explorar
                    IconButton(onClick = onExploreClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Explorar productos"
                        )
                    }
                    // Menú desplegable con más opciones
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Estadísticas") },
                            onClick = {
                                showMenu = false
                                onStatisticsClick()
                            }
                        )
                        // En la Parte 2 se agrega la opción de Cerrar sesión aquí
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto"
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.errorMessage != null -> {
                ErrorMessage(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadProducts() }
                )
            }
            uiState.products.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "No tienes productos.\nPresiona + para agregar uno.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { product -> product.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
        }
    }
}
