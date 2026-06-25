package com.techflow.app.ui.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.R
import com.techflow.app.data.local.NotificationEntity
import com.techflow.app.ui.components.LoadingIndicator
import com.techflow.app.ui.theme.OnSurfaceLight
import com.techflow.app.ui.theme.OnSurfaceVariantLight
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.ui.theme.SurfaceLight
import com.techflow.app.viewmodel.NotificationHistoryViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// NotificationHistoryScreen - pantalla de historial de notificaciones (funcionalidad extra)
// Muestra todas las notificaciones guardadas localmente: alertas de stock bajo (RF16)
// y notificaciones push recibidas desde Firebase Cloud Messaging (RF18)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationHistoryScreen(
    viewModel: NotificationHistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = OnSurfaceLight
                        )
                    }
                },
                actions = {
                    // Limpia todo el historial de notificaciones
                    IconButton(onClick = { viewModel.clearAll() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Borrar historial",
                            tint = OnSurfaceLight
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.nuevoproducto),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            uiState.notifications.isEmpty() -> {
                // Estado vacío - sin notificaciones registradas todavía
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = OnSurfaceVariantLight,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes notificaciones todavía",
                        color = OnSurfaceVariantLight,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = {
                                notification.productId?.let { productId ->
                                    onProductClick(productId)
                                }
                            }
                        )
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationEntity,
    onClick: () -> Unit
) {
    // Formatea el timestamp guardado en milisegundos al patrón dd/MM/yyyy HH:mm
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val formattedDate = dateFormat.format(java.util.Date(notification.timestamp))

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = notification.productId != null) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceLight)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de campana dentro de un círculo, mismo estilo visual de ProductCard
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryBlue.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurfaceLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariantLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = OnSurfaceVariantLight.copy(alpha = 0.7f)
                )
            }
        }
    }
}
