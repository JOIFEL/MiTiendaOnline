package com.example.mitiendaonline.data.model

data class CartItem(
    val producto: Producto, // El producto es inmutable (val)
    var cantidad: Int      // La cantidad es mutable (var)
) {
    // El subtotal es un getter, se recalcula cada vez que se accede a él
    // Esto asegura que siempre esté actualizado con la cantidad actual
    val subtotal: Double
        get() = producto.precio * cantidad
}