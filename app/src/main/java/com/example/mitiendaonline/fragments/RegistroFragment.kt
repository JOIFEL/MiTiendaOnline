// app/java/com/your_package_name/fragments/RegistroFragment.kt
package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // Para mostrar mensajes temporales

import com.example.mitiendaonline.R // Importa la clase R para acceder a los recursos (IDs, layouts)
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.data.model.Usuario
import com.example.mitiendaonline.databinding.FragmentRegistroBinding // Importa la clase de binding (FragmentRegistroBinding)

// Importa el LoginFragment al que queremos navegar de regreso
import com.example.mitiendaonline.fragments.LoginFragment


// RegistroFragment hereda de la clase Fragment
class RegistroFragment : Fragment() {

    // Declarar la variable para View Binding
    private var _binding: FragmentRegistroBinding? = null

    // Propiedad para acceder al binding de forma segura
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este Fragment usando View Binding
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        val view = binding.root // Obtiene la vista raíz

        // --- Configuración inicial de UI si es necesaria ---

        return view // Devuelve la vista inflada
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Lógica para encontrar elementos UI y configurar acciones ---

        // Configurar OnClickListener para el botón de Registrarse
        binding.buttonRegister.setOnClickListener {

            val nombre = binding.editTextFullName.text.toString().trim()
            val correo = binding.editTextEmail.text.toString().trim()
            val contraseña = binding.editTextPassword.text.toString()
            val confirmarContraseña = binding.editTextConfirmPassword.text.toString()

            if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contraseña != confirmarContraseña) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dao = daoUsuario(requireContext())
            val existente = dao.getUserByEmail(correo)

            if (existente != null) {
                Toast.makeText(requireContext(), "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(nombre = nombre, correo = correo, contraseña = contraseña)
            val insertado = dao.insertar(usuario)

            if (insertado) {
                Toast.makeText(requireContext(), "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Error al registrar. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
            }

        }

        binding.buttonGoToLogin.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

    }
}



