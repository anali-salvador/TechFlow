package com.techflow.app.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techflow.app.R
import com.techflow.app.data.local.STOCK_MINIMO_GLOBAL
import com.techflow.app.domain.model.Product
import com.techflow.app.ui.theme.PrimaryBlue
import com.techflow.app.viewmodel.InventoryViewModel

// ProductFormScreen - Pantalla 4 del expediente técnico (Formulario Agregar / Editar)
// Campos: nombre, categoría (dropdown), marca, precio, cantidad, stock mínimo (solo lectura), descripción
// El stock mínimo se MUESTRA en el formulario pero es de solo lectura: su valor es fijo del sistema
// (STOCK_MINIMO_GLOBAL) igual para todos los productos, el usuario no puede escribirlo ni cambiarlo
// Validación de campos obligatorios antes de guardar
// Si productId != null es edición (precarga datos), si es null es creación nueva
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Int? = null,
    viewModel: InventoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    // Si es edición, carga el producto existente
    LaunchedEffect(productId) {
        if (productId != null && productId != -1) {
            viewModel.loadProductById(productId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val isEditing = productId != null && productId != -1

    // Estados locales para cada campo del formulario
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("1") }
    var descripcion by remember { mutableStateOf("") }
    // Estado para controlar el dropdown de categorías
    var expandedCategoria by remember { mutableStateOf(false) }
    // Estado para controlar el dropdown de marcas
    var expandedMarca by remember { mutableStateOf(false) }
    // Estado para mostrar errores de validación
    var showErrors by remember { mutableStateOf(false) }
    // Controla si ya se precargaron los datos en modo edición
    var dataLoaded by remember { mutableStateOf(false) }

    // Categorías disponibles según el expediente técnico
    val categorias = listOf("Laptop", "Celular", "Accesorio", "Componente", "Tablet", "Monitor", "Periférico", "Otro")

    // Marcas tecnológicas disponibles
    val marcas = listOf("Lenovo", "HP", "Dell", "Asus", "Acer", "Apple", "Samsung", "Logitech", "Razer", "MSI", "Huawei", "Xiaomi", "Sony", "LG", "Corsair", "HyperX", "Kingston", "Western Digital", "Seagate", "Otra")

    // Colores corporativos de alto contraste para el formulario neón
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedLabelColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = PrimaryBlue,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.Black.copy(alpha = 0.6f),
        unfocusedContainerColor = Color.Black.copy(alpha = 0.4f)
    )

    // Si es edición y el producto ya se cargó, precarga los campos
    LaunchedEffect(uiState.selectedProduct) {
        if (isEditing && uiState.selectedProduct != null && !dataLoaded) {
            val product = uiState.selectedProduct!!
            nombre = product.nombre
            categoria = product.categoria
            marca = product.marca
            precio = product.precio.toString()
            cantidad = product.cantidad.toString()
            descripcion = product.descripcion ?: ""
            dataLoaded = true
        }
    }

    // Validaciones de campos obligatorios
    val isNombreValid = nombre.isNotBlank()
    val isCategoriaValid = categoria.isNotBlank()
    val isMarcaValid = marca.isNotBlank()
    val isPrecioValid = precio.toDoubleOrNull() != null && precio.toDouble() > 0
    val isCantidadValid = cantidad.toIntOrNull() != null && cantidad.toInt() >= 0

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Producto" else "Nuevo Producto",
                        style = MaterialTheme.typography.titleLarge,
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
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.AddCircle,
                        contentDescription = if (isEditing) "Editando producto" else "Nuevo producto",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
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
            // verticalScroll permite hacer scroll si el formulario es más largo que la pantalla
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Campo Nombre - obligatorio
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto *") },
                isError = showErrors && !isNombreValid,
                supportingText = if (showErrors && !isNombreValid) {
                    { Text("El nombre es obligatorio") }
                } else null,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )

            // Campo Categoría - dropdown con opciones predefinidas
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = it }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    isError = showErrors && !isCategoriaValid,
                    supportingText = if (showErrors && !isCategoriaValid) {
                        { Text("Selecciona una categoría") }
                    } else null,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = fieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                categoria = option
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            // Campo Marca - dropdown con opciones predefinidas
            ExposedDropdownMenuBox(
                expanded = expandedMarca,
                onExpandedChange = { expandedMarca = it }
            ) {
                OutlinedTextField(
                    value = marca,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Marca *") },
                    isError = showErrors && !isMarcaValid,
                    supportingText = if (showErrors && !isMarcaValid) {
                        { Text("Selecciona una marca") }
                    } else null,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMarca) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    colors = fieldColors
                )
                ExposedDropdownMenu(
                    expanded = expandedMarca,
                    onDismissRequest = { expandedMarca = false }
                ) {
                    marcas.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                marca = option
                                expandedMarca = false
                            }
                        )
                    }
                }
            }

            // Campo Precio - solo números decimales
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio (S/.) *") },
                isError = showErrors && !isPrecioValid,
                supportingText = if (showErrors && !isPrecioValid) {
                    { Text("Ingresa un precio válido") }
                } else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )

            // Cantidad y Stock mínimo en la misma fila
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Campo Cantidad - solo números enteros
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad *") },
                    isError = showErrors && !isCantidadValid,
                    supportingText = if (showErrors && !isCantidadValid) {
                        { Text("Inválido") }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors
                )
                // Campo Stock Mínimo - de solo lectura, valor fijo del sistema (STOCK_MINIMO_GLOBAL)
                // El usuario puede ver el valor pero no puede escribirlo ni modificarlo
                OutlinedTextField(
                    value = STOCK_MINIMO_GLOBAL.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Stock mín.") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = fieldColors
                )
            }

            // Campo Descripción - opcional, multilinea
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 4,
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Guardar - valida campos antes de guardar
            Button(
                onClick = {
                    showErrors = true
                    // Si todos los campos obligatorios son válidos, guarda el producto
                    if (isNombreValid && isCategoriaValid && isMarcaValid &&
                        isPrecioValid && isCantidadValid
                    ) {
                        val product = Product(
                            id = if (isEditing) productId!! else 0,
                            firestoreId = if (isEditing) uiState.selectedProduct?.firestoreId ?: "" else "",
                            nombre = nombre.trim(),
                            categoria = categoria,
                            marca = marca.trim(),
                            precio = precio.toDouble(),
                            cantidad = cantidad.toInt(),
                            // Stock mínimo fijo del sistema, igual para todos los productos
                            stockMinimo = STOCK_MINIMO_GLOBAL,
                            descripcion = descripcion.trim().ifBlank { null },
                            userId = "",
                            fechaRegistro = if (isEditing) uiState.selectedProduct?.fechaRegistro ?: System.currentTimeMillis() else System.currentTimeMillis()
                        )
                        if (isEditing) {
                            viewModel.updateProduct(product)
                        } else {
                            viewModel.addProduct(product)
                        }
                        onBackClick() // Vuelve a la lista después de guardar
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isEditing) "Actualizar Producto" else "Guardar Producto",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Botón Cancelar - vuelve sin guardar
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, PrimaryBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
            ) {
                Text(text = "Cancelar")
            }
            }
        }
    }
}
