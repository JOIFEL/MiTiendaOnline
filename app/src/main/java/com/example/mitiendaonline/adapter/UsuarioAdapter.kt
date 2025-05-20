package com.example.mitiendaonline.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.model.Usuario

class UsuarioAdapter(
    private val usuarios: List<Usuario>,
    private val onEditar: (Usuario) -> Unit,
    private val onEliminar: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    private var usuariosOriginal: List<Usuario> = usuarios
    private var usuariosFiltrados: List<Usuario> = usuarios

    inner class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.tvNombreUsuario)
        val btnEditar = view.findViewById<ImageView>(R.id.ivEditar)
        val btnEliminar = view.findViewById<ImageView>(R.id.ivEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuariosFiltrados[position]
        holder.nombre.text = usuario.correo
        holder.btnEditar.setOnClickListener { onEditar(usuario) }
        holder.btnEliminar.setOnClickListener { onEliminar(usuario) }
    }

    override fun getItemCount(): Int = usuariosFiltrados.size

    fun actualizarLista(nuevaLista: List<Usuario>) {
        usuariosOriginal = nuevaLista
        usuariosFiltrados = nuevaLista
        notifyDataSetChanged()
    }

    fun filtrar(query: String) {
        usuariosFiltrados = if (query.isEmpty()) {
            usuariosOriginal
        } else {
            usuariosOriginal.filter {
                it.nombre.contains(query, ignoreCase = true) ||
                        it.correo.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
