package com.example.mitiendaonline.data.model

data class Usuario(
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contraseña: String,
    val rol: String,
    val isGoogleUser: Boolean = false
)