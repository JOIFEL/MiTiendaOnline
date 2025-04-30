// app/java/com/your_package_name/data/Producto.kt
package com.example.mitiendaonline.data

import androidx.annotation.DrawableRes // Importa la anotación para indicar que es un recurso drawable

// Data class para representar un producto
data class Producto(
    val id: Int, // Identificador único del producto (simulado)
    val nombre: String, // Nombre del producto
    val precio: Double, // Precio del producto
    @DrawableRes val imagenResId: Int // ID del recurso drawable para la imagen del producto
)