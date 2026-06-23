package com.techflow.app.util

import java.util.Locale

/**
 * Extrae un nombre amigable a partir del correo del usuario.
 * Ejemplo: "anali.salvador@tecsup.edu.pe" -> "Anali Salvador"
 */
fun extraerNombreDesdeCorreo(correo: String?): String {
    if (correo.isNullOrBlank()) return "Invitado"
    
    // 1. Obtener la parte antes del @
    val parteNombre = correo.substringBefore("@")
    
    // 2. Reemplazar puntos, guiones y guiones bajos por espacios
    val limpio = parteNombre.replace(Regex("[.\\-_]"), " ")
    
    // 3. Capitalizar cada palabra (Ej: "anali" -> "Anali")
    val capitalizado = limpio.split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { palabra ->
            palabra.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
            }
        }
    
    // 4. Saludo corto: si tiene más de 2 nombres/apellidos, usamos solo el primero
    val palabras = capitalizado.split(" ")
    return if (palabras.size > 2) palabras[0] else capitalizado
}
