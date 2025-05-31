package com.example.mitiendaonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.BuscarBarView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.adapter.UsuarioAdapter
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.data.model.Usuario
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton

class UsuarioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var btnAgregar: MaterialButton
    private val listaUsuarios = mutableListOf<Usuario>()

    private val ROLES = listOf("cliente", "admin")

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
                    Toast.makeText(requireContext(), "Este usuario no puede ser editado.", Toast.LENGTH_SHORT).show()
                } else {
                    mostrarDialogoEditarUsuario(usuario)
                }
            },
            onEliminar = { usuario ->

                if (usuario.correo == "admin@admin.com") {
                    Toast.makeText(requireContext(), "Este usuario no puede ser eliminado.", Toast.LENGTH_SHORT).show()
                } else {
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

    override fun onResume() {
        super.onResume()
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        listaUsuarios.clear()
        listaUsuarios.addAll(daoUsuario(requireContext()).obtenerTodosLosUsuarios())
        adapter.actualizarLista(listaUsuarios)
    }

    private fun mostrarDialogoAgregarUsuario() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_usuario, null)

        val tilNombre = dialogView.findViewById<TextInputLayout>(R.id.tilNombre)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        val tilCorreo = dialogView.findViewById<TextInputLayout>(R.id.tilCorreo)
        val etCorreo = dialogView.findViewById<TextInputEditText>(R.id.etCorreo)
        val tilContrasena = dialogView.findViewById<TextInputLayout>(R.id.tilContrasena)
        val etContrasena = dialogView.findViewById<TextInputEditText>(R.id.etContrasena)
        val tilConfirmarContrasena = dialogView.findViewById<TextInputLayout>(R.id.tilConfirmarContrasena)
        val etConfirmarContrasena = dialogView.findViewById<TextInputEditText>(R.id.etConfirmarContrasena)

        val tilRol = dialogView.findViewById<TextInputLayout>(R.id.tilRol)
        val autoCompleteTextViewRol = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewRol)

        val btnGuardar = dialogView.findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = dialogView.findViewById<MaterialButton>(R.id.btnCancelar)

        val adapterRoles = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, ROLES)
        autoCompleteTextViewRol.setAdapter(adapterRoles)
        autoCompleteTextViewRol.setText(ROLES[0], false)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contraseña = etContrasena.text.toString().trim()
            val confirmarContraseña = etConfirmarContrasena.text.toString().trim()
            val rol = autoCompleteTextViewRol.text.toString().lowercase()

            var isValid = true

            if (nombre.isEmpty()) { tilNombre.error = "El nombre es obligatorio"; isValid = false } else { tilNombre.error = null }
            if (correo.isEmpty()) { tilCorreo.error = "El correo es obligatorio"; isValid = false }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { tilCorreo.error = "Formato de correo no válido"; isValid = false }
            else { tilCorreo.error = null }
            if (contraseña.isEmpty()) { tilContrasena.error = "La contraseña es obligatoria"; isValid = false }
            else if (contraseña.length < 6) { tilContrasena.error = "La contraseña debe tener al menos 6 caracteres"; isValid = false }
            else { tilContrasena.error = null }
            if (confirmarContraseña.isEmpty()) { tilConfirmarContrasena.error = "Confirma la contraseña"; isValid = false }
            else if (contraseña != confirmarContraseña) { tilConfirmarContrasena.error = "Las contraseñas no coinciden"; isValid = false }
            else { tilConfirmarContrasena.error = null }
            if (rol.isEmpty() || !ROLES.contains(rol)) { tilRol.error = "Debes seleccionar un rol"; isValid = false } else { tilRol.error = null }

            if (isValid) {

                if (correo == "admin@admin.com") {
                    Toast.makeText(requireContext(), "No puedes crear un usuario con el correo admin por defecto.", Toast.LENGTH_SHORT).show()
                } else {
                    val nuevoUsuario = Usuario(0, nombre, correo, contraseña, rol, isGoogleUser = false) // Nuevo usuario no es de Google por defecto
                    val dao = daoUsuario(requireContext())

                    if (dao.existeCorreo(correo)) {
                        tilCorreo.error = "El correo ya está en uso"
                    } else {
                        val insertadoId = dao.insertar(nuevoUsuario)
                        if (insertadoId != 1L) {
                            Toast.makeText(requireContext(), "Usuario agregado", Toast.LENGTH_SHORT).show()
                            cargarUsuarios()
                            alertDialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Error al agregar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {


        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_usuario, null)

        val tilNombre = dialogView.findViewById<TextInputLayout>(R.id.tilNombre)
        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)
        val tilCorreo = dialogView.findViewById<TextInputLayout>(R.id.tilCorreo)
        val etCorreo = dialogView.findViewById<TextInputEditText>(R.id.etCorreo)
        val tilContrasena = dialogView.findViewById<TextInputLayout>(R.id.tilContrasena)
        val etContrasena = dialogView.findViewById<TextInputEditText>(R.id.etContrasena)
        val tilConfirmarContrasena = dialogView.findViewById<TextInputLayout>(R.id.tilConfirmarContrasena)
        val etConfirmarContrasena = dialogView.findViewById<TextInputEditText>(R.id.etConfirmarContrasena)

        val tilRol = dialogView.findViewById<TextInputLayout>(R.id.tilRol)
        val autoCompleteTextViewRol = dialogView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewRol)

        val btnGuardar = dialogView.findViewById<MaterialButton>(R.id.btnGuardar)
        val btnCancelar = dialogView.findViewById<MaterialButton>(R.id.btnCancelar)

        etNombre.setText(usuario.nombre)
        etCorreo.setText(usuario.correo)
        etContrasena.setText(usuario.contraseña)
        etConfirmarContrasena.setText(usuario.contraseña)

        val adapterRoles = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, ROLES)
        autoCompleteTextViewRol.setAdapter(adapterRoles)

        val rolIndex = ROLES.indexOf(usuario.rol)
        if (rolIndex != -1) {
            autoCompleteTextViewRol.setText(ROLES[rolIndex], false)
        } else {
            autoCompleteTextViewRol.setText(ROLES[0], false)
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contraseña = etContrasena.text.toString().trim()
            val confirmarContraseña = etConfirmarContrasena.text.toString().trim()
            val rol = autoCompleteTextViewRol.text.toString().lowercase()

            var isValid = true

            if (nombre.isEmpty()) { tilNombre.error = "El nombre es obligatorio"; isValid = false } else { tilNombre.error = null }
            if (correo.isEmpty()) { tilCorreo.error = "El correo es obligatorio"; isValid = false }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { tilCorreo.error = "Formato de correo no válido"; isValid = false }
            else { tilCorreo.error = null }
            if (contraseña.isEmpty()) { tilContrasena.error = "La contraseña es obligatoria"; isValid = false }
            else if (contraseña.length < 6) { tilContrasena.error = "La contraseña debe tener al menos 6 caracteres"; isValid = false }
            else { tilContrasena.error = null }
            if (confirmarContraseña.isEmpty()) { tilConfirmarContrasena.error = "Confirma la contraseña"; isValid = false }
            else if (contraseña != confirmarContraseña) { tilConfirmarContrasena.error = "Las contraseñas no coinciden"; isValid = false }
            else { tilConfirmarContrasena.error = null }
            if (rol.isEmpty() || !ROLES.contains(rol)) { tilRol.error = "Debes seleccionar un rol"; isValid = false } else { tilRol.error = null }

            if (isValid) {
                if (correo != usuario.correo && daoUsuario(requireContext()).existeCorreo(correo)) {
                    tilCorreo.error = "El correo ya está en uso"
                } else {

                    val usuarioActualizado = usuario.copy(
                        nombre = nombre,
                        correo = correo,
                        contraseña = contraseña,
                        rol = rol,
                        isGoogleUser = usuario.isGoogleUser
                    )
                    val dao = daoUsuario(requireContext())
                    val actualizado = dao.actualizarUsuario(usuarioActualizado)
                    if (actualizado) {
                        Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()
                        cargarUsuarios()
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnCancelar.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}