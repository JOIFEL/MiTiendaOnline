// app/java/com/your_package_name/adapter/ProductoAdapter.kt
package com.example.mitiendaonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.data.Producto // Importa la data class Producto
import com.example.mitiendaonline.databinding.ItemProductBinding // Importa la clase de binding para item_product.xml
// Opcional: Importar Toast u otros listeners si añades interacción aquí (lo haremos en la Parte 14)
// import android.widget.Toast


// Declaramos la clase ProductoAdapter, hereda de RecyclerView.Adapter
// Le indicamos que usará nuestro ProductoViewHolder interno
class ProductoAdapter(private val listaProductos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    // ----------- Clase Interna ViewHolder -----------
    // Representa la vista de un solo elemento de la lista (un ítem de producto)
    inner class ProductoViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) { // itemView es la vista raíz del item_product.xml

        // Método para "enlazar" (bind) los datos de un Producto con las vistas del ViewHolder
        fun bind(producto: Producto) {
            // Usamos binding para acceder a las vistas del item_product.xml por su ID
            binding.imageViewProductImage.setImageResource(producto.imagenResId) // Establece la imagen
            binding.textViewProductName.text = producto.nombre // Establece el nombre
            binding.textViewProductPrice.text = "$${producto.precio}" // Establece el precio (formato simple con $)

            // Opcional: Configurar click listener para el ícono "Añadir al Carrito" si la lógica va aquí
            // binding.imageViewAddToCart.setOnClickListener {
            //     // Código cuando se hace clic en "Añadir" para este producto
            //     Toast.makeText(binding.root.context, "Añadido ${producto.nombre} al carrito", Toast.LENGTH_SHORT).show()
            // }

            // Opcional: Configurar click listener para el ítem completo si quieres ir a una pantalla de detalle
            // binding.root.setOnClickListener {
            //     // Código cuando se hace clic en el ítem completo
            // }
        }
    }

    // ----------- Métodos del Adaptador -----------

    // 1. Crea nuevos ViewHolders (se llama cuando el RecyclerView necesita un nuevo ítem)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        // Infla el layout item_product.xml usando View Binding
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Crea y devuelve un nuevo ProductoViewHolder usando el binding inflado
        return ProductoViewHolder(binding)
    }

    // 2. Reemplaza el contenido de la vista de un ítem existente (se llama cuando un ítem se hace visible)
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        // Obtiene el objeto Producto en la posición actual de la lista
        val producto = listaProductos[position]
        // Llama al método bind() del ViewHolder para mostrar los datos de este producto
        holder.bind(producto)
    }

    // 3. Devuelve el tamaño total de la lista de datos
    override fun getItemCount(): Int {
        return listaProductos.size // El número de ítems es simplemente el tamaño de la lista que le pasamos al adaptador
    }
}