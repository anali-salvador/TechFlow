package com.techflow.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// ConfirmDeleteDialog - diálogo de confirmación antes de eliminar un producto
// El expediente técnico pide un diálogo de confirmación al eliminar (Pantalla 3 - Detalle)
// Recibe el nombre del producto para mostrarlo en el mensaje
@Composable
fun ConfirmDeleteDialog(
    productName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Eliminar producto")
        },
        text = {
            Text(text = "¿Estás seguro de que deseas eliminar \"$productName\"? Esta acción no se puede deshacer.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}
