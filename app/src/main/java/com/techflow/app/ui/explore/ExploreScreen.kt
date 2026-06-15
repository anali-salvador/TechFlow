package com.techflow.app.ui.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.ui.components.ErrorMessage
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.viewmodel.ExploreViewModel

// ExploreScreen - Pantalla 5 del expediente técnico (Explorar / Búsqueda por API)
// Campo de búsqueda para escribir el nombre del producto
// Lista de resultados obtenidos desde la API con Retrofit
// Estado de carga mientras se obtiene la respuesta
// Mensaje de error si no hay conexión
// Botón Agregar en cada resultado para incorporarlo al inventario local
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Muestra un Snackbar cuando se agrega un producto exitosamente
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.productAdded) {
        if (uiState.productAdded) {
            snackbarHostState.showSnackbar("Producto agregado al inventario")
            viewModel.resetProductAdded()
        }
    }

    // Carga los productos al entrar a la pantalla por primera vez
    LaunchedEffect(Unit) {
        viewModel.searchProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explorar Productos") },
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de búsqueda con campo de texto y botón buscar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    label = { Text("Buscar producto tecnológico") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                )
                // Botón que dispara la búsqueda en la API
                FilledIconButton(
                    onClick = { viewModel.searchProducts() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                }
            }

            // Contenido según el estado actual
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                uiState.errorMessage != null -> {
                    ErrorMessage(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.searchProducts() }
                    )
                }
                uiState.apiProducts.isEmpty() && !uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron productos.\nIntenta con otra búsqueda.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    // Lista de productos de la API
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.apiProducts,
                            key = { it.id }
                        ) { apiProduct ->
                            // Card de cada producto de la API
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = apiProduct.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Categoría: ${apiProduct.category}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = apiProduct.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "S/. ${"%.2f".format(apiProduct.price)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        // Botón Agregar al inventario local
                                        FilledTonalButton(
                                            onClick = {
                                                viewModel.addProductToInventory(
                                                    title = apiProduct.title,
                                                    price = apiProduct.price,
                                                    description = apiProduct.description,
                                                    category = apiProduct.category
                                                )
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Agregar",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Agregar")
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
}
