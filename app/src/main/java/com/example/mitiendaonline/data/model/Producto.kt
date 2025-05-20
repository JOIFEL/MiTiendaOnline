// app/java/com/your_package_name/data/Producto.kt
package com.example.mitiendaonline.data.model

import java.io.Serializable


data class Producto(
    val id: Int, // Identificador único del producto (simulado)
    val nombre: String, // Nombre del producto
    val descripcion: String,
    val precio: Double, // Precio del producto
    val stock: Int,
    val imagenUri: String? // ID del recurso drawable para la imagen del producto

): Serializable