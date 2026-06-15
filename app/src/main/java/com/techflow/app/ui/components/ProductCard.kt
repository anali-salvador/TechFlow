package com.techflow.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techflow.app.domain.model.Product
import com.techflow.app.ui.theme.*

// ProductCard - tarjeta profesional de producto con ícono de categoría y badge de stock
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    // Color según la categoría del producto
    val categoryColor = getCategoryColor(product.categoria)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono circular con la inicial de la categoría
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = categoryColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = product.categoria.take(1).uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Información del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${product.categoria} · ${product.marca}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "S/. ${"%.2f".format(product.precio)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Badge de stock y flecha
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                StockBadge(
                    cantidad = product.cantidad,
                    stockMinimo = product.stockMinimo
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Ver detalle",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Función que retorna el color según la categoría del producto
fun getCategoryColor(categoria: String): Color {
    return when (categoria.lowercase()) {
        "laptop" -> CategoryLaptop
        "celular" -> CategoryCelular
        "accesorio" -> CategoryAccesorio
        "componente" -> CategoryComponente
        "tablet" -> CategoryTablet
        "monitor" -> CategoryMonitor
        "periférico" -> CategoryPeriferico
        else -> CategoryOtro
    }
}
