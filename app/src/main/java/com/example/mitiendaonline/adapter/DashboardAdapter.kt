package com.example.mitiendaonline.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.data.model.Producto
import com.example.mitiendaonline.databinding.ItemProductCrudBinding
import com.example.mitiendaonline.fragments.EditarProductoFragment

class DashboardAdapter(private val listaProductos: MutableList<Producto>) :
    RecyclerView.Adapter<DashboardAdapter.ProductoViewHolder>() {



    inner class ProductoViewHolder(private val binding: ItemProductCrudBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {
            if (producto.imagenUri != null) {
                binding.imageViewProductImage.setImageURI(android.net.Uri.parse(producto.imagenUri))
            } else {
                binding.imageViewProductImage.setImageResource(R.drawable.ic_launcher_background)
            }
            binding.textViewProductName.text = producto.nombre
            binding.textViewProductPrice.text = "$${producto.precio}"

            binding.imageViewDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Elimina de la base de datos
                    val dao = daoProducto(binding.root.context)
                    dao.eliminar(producto.id)
                    // Elimina de la lista y notifica
                    listaProductos.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            binding.imageViewEdit.setOnClickListener {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val producto = listaProductos[position]
                    val fragment = EditarProductoFragment().apply {
                        arguments = Bundle().apply {
                            putInt("id", producto.id)
                            putString("nombre", producto.nombre)
                            putString("descripcion", producto.descripcion)
                            putDouble("precio", producto.precio)
                            putInt("stock", producto.stock)
                            putString("imagenUri", producto.imagenUri)
                        }
                    }
                    val activity = binding.root.context as? AppCompatActivity
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, fragment)
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductCrudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    fun actualizarLista(nuevaLista: List<Producto>) {
        listaProductos.clear()
        listaProductos.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}