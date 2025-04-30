// app/java/com/your_package_name/adapter/CarritoAdapter.kt
package com.example.mitiendaonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast // Para mostrar mensajes temporales al eliminar (demo)
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.data.CartItem // Importa la data class CartItem
import com.example.mitiendaonline.databinding.ItemCartBinding // Importa la clase de binding para item_cart.xml

// Declaramos la clase CarritoAdapter, hereda de RecyclerView.Adapter
// Le indicamos que usará nuestro CarritoViewHolder interno
// Opcional: Añadimos un listener en el constructor para comunicar acciones al Fragment
class CarritoAdapter(
    private val listaItemsCarrito: MutableList<CartItem>, // Usamos MutableList si vamos a eliminar ítems
    private val onItemRemoved: (CartItem) -> Unit // Definimos una función lambda para notificar al Fragment cuando un ítem se elimina
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    // ----------- Clase Interna ViewHolder -----------
    // Representa la vista de un solo elemento de la lista (un ítem del carrito)
    inner class CarritoViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) { // itemView es la vista raíz del item_cart.xml

        // Método para "enlazar" (bind) los datos de un CartItem con las vistas del ViewHolder
        fun bind(cartItem: CartItem) {
            // Usamos binding para acceder a las vistas de item_cart.xml
            binding.imageViewCartItemImage.setImageResource(cartItem.producto.imagenResId) // Imagen del producto
            binding.textViewCartItemName.text = cartItem.producto.nombre // Nombre del producto
            binding.textViewCartItemPricePerUnit.text = "Precio: $${cartItem.producto.precio}" // Precio unitario
            binding.textViewCartItemQuantity.text = "Cantidad: ${cartItem.cantidad}" // Cantidad
            binding.textViewCartItemSubtotal.text = "Subtotal: $${String.format("%.2f", cartItem.subtotal)}" // Subtotal por ítem (formato con 2 decimales)

            // Configurar click listener para el ícono "Eliminar Ítem"
            binding.imageViewRemoveItem.setOnClickListener {
                // Obtener la posición del ítem en el adaptador
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) { // Asegurarse de que la posición sea válida
                    val removedItem = listaItemsCarrito[position] // Obtener el ítem a eliminar

                    // Notificar al Fragment (o quien sea el listener) que este ítem debe ser eliminado
                    onItemRemoved(removedItem)

                    // Las operaciones de modificación de la lista (eliminar, añadir) y notificar
                    // al adaptador (`notifyItemRemoved`, `notifyDataSetChanged`) se harán
                    // en la función `onItemRemoved` que se pasa desde el Fragment para mantener
                    // la lógica de los datos fuera del adaptador directamente.
                }
            }

            // Opcional: Configurar click listeners para +/- cantidad si existieran esos botones
            // binding.buttonIncreaseQuantity.setOnClickListener { ... }
            // binding.buttonDecreaseQuantity.setOnClickListener { ... }
        }
    }

    // ----------- Métodos del Adaptador -----------

    // 1. Crea nuevos ViewHolders (se llama cuando el RecyclerView necesita un nuevo ítem)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        // Infla el layout item_cart.xml usando View Binding
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Crea y devuelve un nuevo CarritoViewHolder usando el binding inflado
        return CarritoViewHolder(binding)
    }

    // 2. Reemplaza el contenido de la vista de un ítem existente
    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        // Obtiene el objeto CartItem en la posición actual
        val cartItem = listaItemsCarrito[position]
        // Llama al método bind() del ViewHolder para mostrar los datos
        holder.bind(cartItem)
    }

    // 3. Devuelve el tamaño total de la lista de datos
    override fun getItemCount(): Int {
        return listaItemsCarrito.size // Tamaño de la lista de ítems del carrito
    }

    // Opcional: Método para actualizar la lista si los datos cambian desde fuera
    fun updateList(newList: List<CartItem>) {
        listaItemsCarrito.clear() // Limpia la lista actual
        listaItemsCarrito.addAll(newList) // Añade los nuevos ítems
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado para que se redibuje
    }
}