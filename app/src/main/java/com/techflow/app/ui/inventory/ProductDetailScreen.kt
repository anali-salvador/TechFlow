package com.techflow.app.ui.inventory

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.techflow.app.R
import com.techflow.app.domain.model.Product
import com.techflow.app.ui.components.ConfirmDeleteDialog
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.ui.components.StockBadge
import com.techflow.app.ui.components.getCategoryColor
import com.techflow.app.ui.components.getCategoryIcon
import com.techflow.app.ui.theme.ErrorRed
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.ui.theme.SuccessGreen
import com.techflow.app.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    viewModel: InventoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    LaunchedEffect(productId) {
        viewModel.loadProductById(productId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val product: Product? = uiState.selectedProduct

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && product != null) {
        ConfirmDeleteDialog(
            productName = product.nombre,
            onConfirm = {
                viewModel.deleteProduct(product)
                showDeleteDialog = false
                onBackClick()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        containerColor = Color(0xFF020617), // Negro OLED profundo
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ficha Técnica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
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
                actions = {
                    if (product != null) {
                        FilledTonalIconButton(
                            onClick = { onEditClick(product.id) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = PrimaryBlue.copy(alpha = 0.15f),
                                contentColor = PrimaryBlue
                            ),
                            modifier = Modifier.size(38.dp).border(1.dp, PrimaryBlue.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(Icons.Default.Edit, "Editar", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        FilledTonalIconButton(
                            onClick = { showDeleteDialog = true },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = ErrorRed.copy(alpha = 0.15f),
                                contentColor = ErrorRed
                            ),
                            modifier = Modifier.size(38.dp).border(1.dp, ErrorRed.copy(alpha = 0.4f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, "Eliminar", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        if (product == null) {
            LoadingIndicator()
        } else {
            val isLowStock = product.cantidad <= product.stockMinimo

            Box(modifier = Modifier.fillMaxSize()) {
                // Fondo neón completo
                Image(
                    painter = painterResource(id = R.drawable.nuevoproducto),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.3f
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // SECCIÓN: IMAGEN REAL DEL PRODUCTO (HÉROE)
                    StaggeredVisibility(index = 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .padding(16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = PrimaryBlue),
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = getProductImageUrl(product),
                                        contentDescription = product.nombre,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    
                                    // Overlay de categoría neón
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(16.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = getCategoryColor(product.categoria).copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = product.categoria.uppercase(),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // SECCIÓN: INFO PRINCIPAL
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = product.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.marca,
                                style = MaterialTheme.typography.titleMedium,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.size(4.dp).background(Color.White.copy(alpha = 0.3f), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            StockBadge(product.cantidad, product.stockMinimo)
                        }
                    }

                    // SECCIÓN: PRECIO DESTACADO
                    StaggeredVisibility(index = 1) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Precio Sugerido", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                                    Text(
                                        "S/. ${"%.2f".format(product.precio)}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Icon(Icons.Default.Payments, null, tint = SuccessGreen, modifier = Modifier.size(40.dp))
                            }
                        }
                    }

                    // ALERTA DE STOCK (SI APLICA)
                    if (isLowStock) {
                        StaggeredVisibility(index = 2) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ReportProblem, null, tint = ErrorRed)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("¡Inventario Crítico! Reponer pronto.", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // SECCIÓN: DETALLES TÉCNICOS
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Especificaciones", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        
                        DetailRow(Icons.Default.Inventory2, "Stock Disponible", "${product.cantidad} unidades")
                        DetailRow(Icons.Default.NotificationsActive, "Alerta de Stock", "${product.stockMinimo} unidades")
                        
                        if (!product.descripcion.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Descripción", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = product.descripcion!!,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f),
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

// Función inteligente para obtener una imagen real basada en la URL guardada o la categoría
private fun getProductImageUrl(product: Product): String {
    if (!product.imagenUrl.isNullOrBlank()) return product.imagenUrl

    return when (product.categoria.lowercase()) {
        "laptop" -> "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&q=80&w=800"
        "celular" -> "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&q=80&w=800"
        "accesorio", "periférico" -> "https://images.unsplash.com/photo-1546435770-a3e426bf472b?auto=format&fit=crop&q=80&w=800"
        "monitor" -> "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&q=80&w=800"
        "tablet" -> "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&q=80&w=800"
        "componente" -> "https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&q=80&w=800"
        else -> "https://images.unsplash.com/photo-1550009158-9ebf69173e03?auto=format&fit=crop&q=80&w=800"
    }
}
