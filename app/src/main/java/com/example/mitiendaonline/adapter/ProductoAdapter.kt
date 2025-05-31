package com.example.mitiendaonline.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.model.Producto
import com.google.android.material.button.MaterialButton // Importa MaterialButton
import com.google.android.material.imageview.ShapeableImageView // Importa ShapeableImageView
import com.example.mitiendaonline.databinding.ItemProductBinding // Asumo que estás usando ViewBinding para item_product.xml
import java.text.NumberFormat // Para formatear el precio
import java.util.Locale // Para el Locale de Colombia

class ProductoAdapter(
    private val listaProductos: MutableList<Producto>,
    private val onAddToCartClicked: (Producto) -> Unit, // Renombrado para mayor claridad
    private val onItemClicked: (Producto) -> Unit // Nuevo callback para el clic en la tarjeta completa
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    // Formateador de moneda para Pesos Colombianos
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        // Puedes establecer el símbolo de moneda explícitamente si el Locale no lo da por defecto
        // try { currency = java.util.Currency.Currency.getInstance("COP") } catch (e: Exception) { e.printStackTrace() }
    }

    inner class ProductoViewHolder(private val binding: ItemProductBinding) : // Usamos ViewBinding
        RecyclerView.ViewHolder(binding.root) { // binding.root es la vista raíz (MaterialCardView)

        fun bind(producto: Producto) {
            // Imagen del producto
            if (!producto.imagenUri.isNullOrEmpty()) {
                binding.imageViewProductImage.setImageURI(Uri.parse(producto.imagenUri))
            } else {
                binding.imageViewProductImage.setImageResource(R.drawable.ic_image_placeholder) // Usa el placeholder
            }

            // Nombre del producto
            binding.textViewProductName.text = producto.nombre

            // Precio del producto formateado a Pesos Colombianos
            binding.textViewProductPrice.text = currencyFormat.format(producto.precio)

            // --- LISTENERS ---

            // Listener para el botón "Añadir al Carrito" (si tu item_product.xml tiene uno)
            binding.imageViewAddToCart.setOnClickListener { // Asegúrate que este es el ID correcto
                onAddToCartClicked(producto)
            }

            // Listener para la tarjeta completa del ítem (para navegar a detalles del producto)
            binding.root.setOnClickListener {
                onItemClicked(producto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        // Infla el layout item_product.xml usando ViewBinding
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(listaProductos[position])
    }

    override fun getItemCount(): Int = listaProductos.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        listaProductos.clear()
        listaProductos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}