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

    private val onItemClicked: (CartItem) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {

        }

        fun bind(cartItem: CartItem) {

            val uriString = cartItem.producto.imagenUri
            if (uriString != null && uriString.isNotEmpty()) {
                binding.imageViewCartItemImage.setImageURI(android.net.Uri.parse(uriString))
            } else {
                binding.imageViewCartItemImage.setImageResource(R.drawable.ic_image_placeholder)
            }


            binding.textViewCartItemName.text = cartItem.producto.nombre


            binding.textViewCartItemPricePerUnit.text = "Precio unitario: ${currencyFormat.format(cartItem.producto.precio)}"


            binding.textViewQuantity.text = cartItem.cantidad.toString()


            binding.textViewCartItemSubtotal.text = "Subtotal: ${currencyFormat.format(cartItem.subtotal)}"



            binding.root.setOnClickListener {

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