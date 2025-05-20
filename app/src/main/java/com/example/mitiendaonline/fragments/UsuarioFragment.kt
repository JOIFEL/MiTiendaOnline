package com.example.mitiendaonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.BuscarBarView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.adapter.UsuarioAdapter
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.data.model.Usuario

class UsuarioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var btnAgregar: Button
    private val listaUsuarios = mutableListOf<Usuario>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuarios, container, false)

        recyclerView = view.findViewById(R.id.recyclerUsuarios)
        btnAgregar = view.findViewById(R.id.btnAgregarUsuario)
        val buscarBar = view.findViewById<BuscarBarView>(R.id.buscarBar)

        adapter = UsuarioAdapter(listaUsuarios,
            onEditar = { usuario ->
                if (usuario.correo == "admin@admin.com") {
                    Toast.makeText(requireContext(), "Este usuario no puede ser editado", Toast.LENGTH_SHORT).show()
                } else {
                    mostrarDialogoEditarUsuario(usuario)
                }
            },
            onEliminar = { usuario ->
                if (usuario.correo == "admin@admin.com") {
                    Toast.makeText(requireContext(), "Este usuario no puede ser eliminado", Toast.LENGTH_SHORT).show()
                }else {
                    val dao = daoUsuario(requireContext())
                    val eliminado = dao.eliminarUsuario(usuario.id)
                    if (eliminado) {
                        listaUsuarios.remove(usuario)
                        adapter.actualizarLista(listaUsuarios)
                        Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        buscarBar.onQueryChanged = { query ->
            adapter.filtrar(query)
        }

        btnAgregar.setOnClickListener {
            mostrarDialogoAgregarUsuario()
        }

        cargarUsuarios()

        return view
    }

    private fun cargarUsuarios() {
        listaUsuarios.clear()
        listaUsuarios.addAll(daoUsuario(requireContext()).obtenerTodosLosUsuarios())
        adapter.actualizarLista(listaUsuarios)
    }

    private fun mostrarDialogoAgregarUsuario() {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_usuario, null)

        val etNombre = view.findViewById<android.widget.EditText>(R.id.etNombre)
        val etCorreo = view.findViewById<android.widget.EditText>(R.id.etCorreo)
        val etContraseña = view.findViewById<android.widget.EditText>(R.id.etContraseña)
        val spinnerRol = view.findViewById<android.widget.Spinner>(R.id.spinnerRol)

        // Configurar spinner de roles ANTES del AlertDialog
        val roles = listOf("admin", "usuario")
        val adapterSpinner = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRol.adapter = adapterSpinner

        // Mostrar diálogo
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Agregar Usuario")
            .setView(view)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val contraseña = etContraseña.text.toString().trim()
                val rol = spinnerRol.selectedItem.toString().lowercase()

                if (nombre.isNotEmpty() && correo.isNotEmpty() && contraseña.isNotEmpty()) {
                    if (correo == "admin@admin.com") {
                        Toast.makeText(requireContext(), "No puedes crear otro usuario admin por defecto", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    val nuevoUsuario = Usuario(0, nombre, correo, contraseña, rol)
                    val dao = daoUsuario(requireContext())

                    if (dao.existeCorreo(correo)) {
                        Toast.makeText(requireContext(), "El correo ya está en uso", Toast.LENGTH_SHORT).show()
                    } else {
                        val insertado = dao.insertar(nuevoUsuario)
                        if (insertado) {
                            Toast.makeText(requireContext(), "Usuario agregado", Toast.LENGTH_SHORT).show()
                            cargarUsuarios()
                        } else {
                            Toast.makeText(requireContext(), "Error al agregar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_usuario, null)

        val etNombre = view.findViewById<android.widget.EditText>(R.id.etNombre)
        val etCorreo = view.findViewById<android.widget.EditText>(R.id.etCorreo)
        val etContraseña = view.findViewById<android.widget.EditText>(R.id.etContraseña)
        val spinnerRol = view.findViewById<android.widget.Spinner>(R.id.spinnerRol)

        etNombre.setText(usuario.nombre)
        etCorreo.setText(usuario.correo)
        etContraseña.setText(usuario.contraseña)

        val roles = listOf("admin", "usuario")
        val adapterSpinner = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roles)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRol.adapter = adapterSpinner
        spinnerRol.setSelection(roles.indexOf(usuario.rol)) // Selecciona el rol actual del usuario

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Editar Usuario")
            .setView(view)
            .setPositiveButton("Actualizar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val contraseña = etContraseña.text.toString().trim()
                val rol = spinnerRol.selectedItem.toString()

                if (nombre.isNotEmpty() && correo.isNotEmpty() && contraseña.isNotEmpty()) {
                    val usuarioActualizado = usuario.copy(
                        nombre = nombre,
                        correo = correo,
                        contraseña = contraseña,
                        rol = rol
                    )
                    val dao = daoUsuario(requireContext())
                    val actualizado = dao.actualizarUsuario(usuarioActualizado)
                    if (actualizado) {
                        Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

