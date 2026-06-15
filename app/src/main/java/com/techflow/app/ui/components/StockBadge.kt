package com.techflow.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    val textColor = if (isLowStock) Color(0xFFD32F2F) else Color(0xFF388E3C)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "$cantidad uds",
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
