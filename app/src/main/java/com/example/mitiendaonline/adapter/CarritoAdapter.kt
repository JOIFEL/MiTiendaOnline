package com.example.mitiendaonline.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.model.CartItem
import com.example.mitiendaonline.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.Locale

class CarritoAdapter(
    private val listaItemsCarrito: MutableList<CartItem>,
    private val onItemRemoved: (CartItem) -> Unit,
    private val onQuantityIncreased: (CartItem) -> Unit,
    private val onQuantityDecreased: (CartItem) -> Unit,
    // Nuevo: Callback cuando se toca la tarjeta completa (para ver detalles del producto)
    private val onItemClicked: (CartItem) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            // Opcional: Configurar el símbolo de moneda si es necesario
            // try { currency = java.util.Currency.getInstance("COP") } catch (e: Exception) { e.printStackTrace() }
        }

        fun bind(cartItem: CartItem) {
            // Imagen del producto
            val uriString = cartItem.producto.imagenUri
            if (uriString != null && uriString.isNotEmpty()) {
                binding.imageViewCartItemImage.setImageURI(android.net.Uri.parse(uriString))
            } else {
                binding.imageViewCartItemImage.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Nombre del producto
            binding.textViewCartItemName.text = cartItem.producto.nombre

            // Precio unitario formateado
            binding.textViewCartItemPricePerUnit.text = "Precio unitario: ${currencyFormat.format(cartItem.producto.precio)}"

            // Cantidad
            binding.textViewQuantity.text = cartItem.cantidad.toString()

            // Subtotal formateado (precio unitario * cantidad)
            binding.textViewCartItemSubtotal.text = "Subtotal: ${currencyFormat.format(cartItem.subtotal)}"

            // --- LISTENERS ---

            // 1. Listener para la tarjeta completa (para ver detalles del producto)
            binding.root.setOnClickListener {
                // Asegúrate de que el clic no venga de los botones internos
                // Si haces clic en el nombre o en el espacio vacío, se activa este listener.
                // Los botones +/- y eliminar interceptarán sus propios clics.
                onItemClicked(cartItem)
            }

            // 2. Listeners para los botones de cantidad
            binding.buttonPlus.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onQuantityIncreased(listaItemsCarrito[position])
                }
            }

            binding.buttonMinus.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onQuantityDecreased(listaItemsCarrito[position])
                }
            }

            // 3. Listener para el botón de eliminar
            binding.buttonRemoveItem.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemRemoved(listaItemsCarrito[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val cartItem = listaItemsCarrito[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int {
        return listaItemsCarrito.size
    }

    fun updateList(newList: List<CartItem>) {
        listaItemsCarrito.clear()
        listaItemsCarrito.addAll(newList)
        notifyDataSetChanged()
    }
}