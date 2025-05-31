package com.example.mitiendaonline.data.model

import java.io.Serializable


data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val imagenUri: String?

): Serializable