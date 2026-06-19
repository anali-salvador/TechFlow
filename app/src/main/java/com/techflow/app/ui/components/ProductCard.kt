package com.techflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techflow.app.domain.model.Product
import com.techflow.app.ui.theme.*

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(product.categoria)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
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
            // Estructura Izquierda: Icono dinámico según categoría dentro de un círculo vibrante
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(categoryColor.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(product.categoria),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Estructura Central: Nombre, Categoría y Precio
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.categoria} • ${product.marca}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/. ${"%.2f".format(product.precio)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Estructura Derecha: Pastilla de Stock
            Column(horizontalAlignment = Alignment.End) {
                val isLowStock = product.cantidad <= 2
                Surface(
                    color = if (isLowStock) LowStockOrange else HealthyStockGreen,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "${product.cantidad} uds",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

fun getCategoryIcon(categoria: String): ImageVector {
    return when (categoria.lowercase()) {
        "laptop" -> Icons.Default.Laptop
        "celular" -> Icons.Default.Smartphone
        "monitor" -> Icons.Default.Tv
        "tablet" -> Icons.Default.TabletAndroid
        "accesorio" -> Icons.Default.Headset
        "componente" -> Icons.Default.SettingsInputComponent
        else -> Icons.Default.Devices
    }
}

fun getCategoryColor(categoria: String): Color {
    return when (categoria.lowercase()) {
        "laptop" -> CatBlue
        "celular" -> CatPurple
        "accesorio" -> CatOrange
        "componente" -> CatGreen
        "tablet" -> CatCyan
        "monitor" -> CatIndigo
        "periférico" -> CatRose
        else -> CatSlate
    }
}
