package com.techflow.app.ui.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.techflow.app.R
import com.techflow.app.ui.components.ErrorMessage
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.ui.theme.OnSurfaceLight
import com.techflow.app.ui.theme.OnSurfaceVariantLight
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.ui.theme.SurfaceLight
import com.techflow.app.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.productAdded) {
        if (uiState.productAdded) {
            snackbarHostState.showSnackbar("Producto agregado al inventario")
            viewModel.resetProductAdded()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.searchProducts()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Explorar Productos",
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
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.nuevoproducto),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                        shape = RoundedCornerShape(24.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = PrimaryBlue
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceLight,
                            unfocusedContainerColor = SurfaceLight,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = PrimaryBlue.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = PrimaryBlue
                        )
                    )
                    FilledIconButton(
                        onClick = { viewModel.searchProducts() },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    }
                }

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
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(
                                items = uiState.apiProducts,
                                key = { _, item -> item.id }
                            ) { index, apiProduct ->
                                StaggeredVisibility(index = index) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AsyncImage(
                                                model = apiProduct.image,
                                                contentDescription = apiProduct.title,
                                                contentScale = ContentScale.Fit,
                                                placeholder = rememberVectorPainter(Icons.Default.Info),
                                                error = rememberVectorPainter(Icons.Default.Info),
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                                    .padding(4.dp)
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = apiProduct.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = OnSurfaceLight,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = apiProduct.category,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = OnSurfaceVariantLight
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "S/. ${"%.2f".format(apiProduct.price)}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = PrimaryBlue
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            FilledTonalIconButton(
                                                onClick = {
                                                    viewModel.addProductToInventory(
                                                        title = apiProduct.title,
                                                        price = apiProduct.price,
                                                        description = apiProduct.description,
                                                        category = apiProduct.category,
                                                        imageUrl = apiProduct.image
                                                    )
                                                },
                                                shape = CircleShape,
                                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                    containerColor = PrimaryBlue.copy(alpha = 0.2f),
                                                    contentColor = PrimaryBlue
                                                )
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Agregar"
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
    }
}
