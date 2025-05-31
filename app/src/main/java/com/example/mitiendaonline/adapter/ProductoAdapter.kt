package com.example.mitiendaonline.adapter

import android.net.Uri
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.model.Producto

import com.example.mitiendaonline.databinding.ItemProductBinding
import java.text.NumberFormat
import java.util.Locale

class ProductoAdapter(
    private val listaProductos: MutableList<Producto>,
    private val onAddToCartClicked: (Producto) -> Unit,
    private val onItemClicked: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {


    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {

    }

    inner class ProductoViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {

            if (!producto.imagenUri.isNullOrEmpty()) {
                binding.imageViewProductImage.setImageURI(Uri.parse(producto.imagenUri))
            } else {
                binding.imageViewProductImage.setImageResource(R.drawable.ic_image_placeholder)
            }


            binding.textViewProductName.text = producto.nombre


            binding.textViewProductPrice.text = currencyFormat.format(producto.precio)



            binding.imageViewAddToCart.setOnClickListener {
                onAddToCartClicked(producto)
            }


            binding.root.setOnClickListener {
                onItemClicked(producto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {

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