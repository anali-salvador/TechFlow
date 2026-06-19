package com.techflow.app.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.components.ErrorMessage
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.ProductCard
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.viewmodel.InventoryViewModel

// InventoryListScreen - Pantalla 2 del expediente técnico (Pantalla Principal)
// Lista de productos con LazyColumn, botón flotante para agregar
// Barra de navegación inferior con Inventario, Explorar y Estadísticas
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TechFlow - Inventario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* ya estamos en Inventario */ },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inventario") },
                    label = { Text("Inventario") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onExploreClick,
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Explorar") },
                    label = { Text("Explorar") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onStatisticsClick,
                    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Estadísticas") },
                    label = { Text("Estadísticas") }
                )
            }
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
                            text = "No tienes productos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Presiona + para agregar tu primer producto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    itemsIndexed(
                        items = uiState.products,
                        key = { _, product -> product.id }
                    ) { index, product ->
                        StaggeredVisibility(index = index) {
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
}
