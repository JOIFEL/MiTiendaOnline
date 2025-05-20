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

class ProductoAdapter(private val listaProductos: MutableList<Producto>, private val onAddToCart: (Producto) -> Unit
) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProductImage: ImageView = itemView.findViewById(R.id.imageViewProductImage)
        private val textViewProductName: TextView = itemView.findViewById(R.id.textViewProductName)
        private val textViewProductPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)

        private val addToCart: ImageView = itemView.findViewById(R.id.imageViewAddToCart)

        fun bind(producto: Producto) {
            if (!producto.imagenUri.isNullOrEmpty()) {
                imageViewProductImage.setImageURI(Uri.parse(producto.imagenUri))
            } else {
                imageViewProductImage.setImageResource(R.drawable.placeholder_image)
            }
            addToCart.setOnClickListener { onAddToCart(producto) }
            textViewProductName.text = producto.nombre
            textViewProductPrice.text = "$${producto.precio}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductoViewHolder(view)
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