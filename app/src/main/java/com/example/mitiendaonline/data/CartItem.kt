// app/java/com/your_package_name/data/CartItem.kt
package com.example.mitiendaonline.data

// Importa la data class Producto si no está en el mismo paquete (debería estarlo si seguiste el paso 11)
// import com.example.mitiendaonline.data.Producto

// Data class para representar un item en el carrito de compras
data class CartItem(
    val producto: Producto, // Referencia al producto en el carrito
    var cantidad: Int // Cantidad de este producto en el carrito (usamos 'var' porque la cantidad puede cambiar)
) {
    // Puedes añadir propiedades calculadas aquí si es útil, por ejemplo:
    val subtotal: Double
        get() = producto.precio * cantidad // Calcula el subtotal de este ítem

}
