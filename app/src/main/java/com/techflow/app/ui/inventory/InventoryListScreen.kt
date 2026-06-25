package com.techflow.app.ui.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.techflow.app.R
import com.techflow.app.ui.components.ErrorMessage
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.components.ProductCard
import com.techflow.app.ui.components.StaggeredVisibility
import com.techflow.app.ui.theme.HealthyStockGreen
import com.techflow.app.ui.theme.LowStockOrange
import com.techflow.app.ui.theme.OnSurfaceVariantLight
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.util.extraerNombreDesdeCorreo
import com.techflow.app.viewmodel.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    viewModel: InventoryViewModel = hiltViewModel(),
    onProductClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onExploreClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Obtención automática del nombre del usuario desde Firebase Auth
    val userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email
    val nombreUsuario = remember(userEmail) { extraerNombreDesdeCorreo(userEmail) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFF22D3EE).copy(alpha = 0.12f))
                                .border(1.dp, Color(0xFF22D3EE).copy(alpha = 0.3f), RoundedCornerShape(7.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = Color(0xFF22D3EE),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TechFlow",
                            color = Color(0xFF22D3EE),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 17.sp,
                            letterSpacing = 0.3.sp
                        )
                    }
                },
                actions = {
                    // Historial de notificaciones (funcionalidad extra)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .clickable(onClick = onNotificationsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color(0xFF9FB3C2),
                            modifier = Modifier.size(17.dp)
                        )
                        // Punto de no leídas - InventoryUiState aún no expone un conteo real,
                        // así que por ahora queda siempre visible
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22D3EE))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Perfil de usuario (solo lectura)
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Mi perfil",
                            tint = Color(0xFF9FB3C2),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Cerrar sesión - cierra la sesión de Firebase Auth y vuelve a Login
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE24B4A).copy(alpha = 0.10f))
                            .border(0.5.dp, Color(0xFFE24B4A).copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .clickable(onClick = onLogoutClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color(0xFFE57C7C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0E2030), Color(0xFF0B1622))
                        )
                    )
                    .drawBehind {
                        drawLine(
                            color = Color(0xFF22D3EE).copy(alpha = 0.12f),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 0.5.dp.toPx()
                        )
                    }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inventario") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onExploreClick,
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    label = { Text("Explorar") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onStatisticsClick,
                    icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Estadísticas") }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.nuevoproducto),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop,
                alpha = 0.5f
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
            item {
                val lowStockCount = uiState.products.count { it.cantidad <= it.stockMinimo }
                val totalValue = uiState.products.sumOf { it.precio * it.cantidad }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.fondo),
                        contentDescription = null,
                        modifier = Modifier
                            .matchParentSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.4f
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "¡Hola, $nombreUsuario!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF22D3EE)
                        )
                        Text(
                            text = "Gestiona tu stock de forma inteligente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickStatCard(
                                modifier = Modifier.weight(1f),
                                backgroundColor = PrimaryBlue.copy(alpha = 0.15f),
                                value = "${uiState.products.size}",
                                valueColor = PrimaryBlue,
                                label = "Productos"
                            )
                            QuickStatCard(
                                modifier = Modifier.weight(1f),
                                backgroundColor = LowStockOrange.copy(alpha = 0.15f),
                                value = "$lowStockCount",
                                valueColor = LowStockOrange,
                                label = "Stock bajo"
                            )
                            QuickStatCard(
                                modifier = Modifier.weight(1f),
                                backgroundColor = HealthyStockGreen.copy(alpha = 0.15f),
                                value = "S/. ${"%.2f".format(totalValue)}",
                                valueColor = HealthyStockGreen,
                                label = "Valor total"
                            )
                        }
                    }

                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.robot_animation))
                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = com.airbnb.lottie.compose.LottieConstants.IterateForever
                    )
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.TopEnd)
                            .padding(top = 12.dp, end = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = OnSurfaceVariantLight.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Mis Productos",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when {
                uiState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }
                }
                uiState.errorMessage != null -> {
                    item {
                        ErrorMessage(
                            message = uiState.errorMessage!!,
                            onRetry = { viewModel.loadProducts() }
                        )
                    }
                }
                uiState.products.isEmpty() -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay productos registrados",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    itemsIndexed(
                        items = uiState.products,
                        key = { _, product -> product.id }
                    ) { index, product ->
                        StaggeredVisibility(index = index) {
                            ProductCard(product = product) {
                                onProductClick(product.id)
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    value: String,
    valueColor: Color,
    label: String
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariantLight
            )
        }
    }
}
