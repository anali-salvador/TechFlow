package com.techflow.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// StockBadge - indicador visual del stock del producto
// Rojo si la cantidad es menor o igual al stock mínimo (alerta de stock bajo)
// Verde si el stock está por encima del mínimo (stock normal)
// El expediente técnico pide indicador visual rojo para stock bajo en la lista
@Composable
fun StockBadge(
    cantidad: Int,
    stockMinimo: Int
) {
    // Determina si el stock está bajo comparando cantidad con stockMinimo
    val isLowStock = cantidad <= stockMinimo
    // Color rojo para stock bajo, verde para stock normal
    val backgroundColor = if (isLowStock) Color(0xFFFFCDD2) else Color(0xFFC8E6C9)
    val dotColor = if (isLowStock) Color(0xFFD32F2F) else Color(0xFF388E3C)
    val textColor = if (isLowStock) Color(0xFFD32F2F) else Color(0xFF388E3C)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Punto circular indicador de estado
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .padding(0.dp)
            ) {
                Surface(
                    modifier = Modifier.size(7.dp),
                    shape = CircleShape,
                    color = dotColor
                ) {}
            }
            Text(
                text = "$cantidad uds",
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
