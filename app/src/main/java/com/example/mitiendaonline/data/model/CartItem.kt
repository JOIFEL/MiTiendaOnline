package com.example.mitiendaonline.data.model

data class CartItem(
    val producto: Producto,
    var cantidad: Int
) {
    val subtotal: Double
        get() = producto.precio * cantidad
}