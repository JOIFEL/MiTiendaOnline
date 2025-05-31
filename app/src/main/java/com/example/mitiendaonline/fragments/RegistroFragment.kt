package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.data.model.Usuario
import com.example.mitiendaonline.databinding.FragmentRegistroBinding
import com.example.mitiendaonline.fragments.LoginFragment // Asegúrate de que esta importación esté correcta

class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { // Cambiado a View sin ? porque binding.root nunca será nulo
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            // Recoger textos y limpiar espacios
            val nombre = binding.editTextFullName.text.toString().trim()
            val correo = binding.editTextEmail.text.toString().trim()
            val contraseña = binding.editTextPassword.text.toString() // No trim en contraseña por si hay espacios intencionales
            val confirmarContraseña = binding.editTextConfirmPassword.text.toString()

            var isValid = true // Flag para controlar si todas las validaciones pasan

            // --- Validaciones con feedback en TextInputLayout ---

            // Validación de Nombre Completo
            if (nombre.isEmpty()) {
                binding.tilFullName.error = "El nombre es obligatorio"
                isValid = false
            } else {
                binding.tilFullName.error = null // Limpiar error si es válido
            }

            // Validación de Correo Electrónico
            if (correo.isEmpty()) {
                binding.tilEmail.error = "El correo es obligatorio"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { // Formato de correo
                binding.tilEmail.error = "Formato de correo no válido"
                isValid = false
            } else {
                binding.tilEmail.error = null
            }

            // Validación de Contraseña
            if (contraseña.isEmpty()) {
                binding.tilPassword.error = "La contraseña es obligatoria"
                isValid = false
            } else if (contraseña.length < 6) { // Ejemplo: mínimo 6 caracteres
                binding.tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
                isValid = false
            } else {
                binding.tilPassword.error = null
            }

            // Validación de Confirmar Contraseña
            if (confirmarContraseña.isEmpty()) {
                binding.tilConfirmPassword.error = "Confirma la contraseña"
                isValid = false
            } else if (contraseña != confirmarContraseña) {
                binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
                isValid = false
            } else {
                binding.tilConfirmPassword.error = null
            }

            // Si todas las validaciones de UI pasan, proceder con la lógica de negocio
            if (isValid) {
                val dao = daoUsuario(requireContext())

                // **Bloquear registro de admin por defecto (Prioridad de lógica)**
                if (correo == "admin@admin.com") {
                    Toast.makeText(requireContext(), "No puedes registrar un usuario con el correo admin por defecto.", Toast.LENGTH_LONG).show()
                    binding.tilEmail.error = "Correo reservado" // Mostrar error visual
                    return@setOnClickListener
                }

                // Verificar si el correo ya está registrado
                if (dao.existeCorreo(correo)) {
                    binding.tilEmail.error = "Este correo ya está registrado"
                    Toast.makeText(requireContext(), "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Crear el objeto Usuario (por defecto isGoogleUser es false)
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    contraseña = contraseña,
                    rol = "cliente", // El rol por defecto para los registros es "cliente"
                    isGoogleUser = false // Por defecto, no es un usuario de Google
                )

                // Insertar el usuario y verificar el ID retornado (Prioridad de ID)
                val insertadoId = dao.insertar(nuevoUsuario)

                if (insertadoId != -1L) { // Comprobar si la inserción fue exitosa
                    Toast.makeText(requireContext(), "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                    // Navegar de regreso a la pantalla de Login
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al registrar. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Configurar OnClickListener para el botón "Ya tienes cuenta? Inicia Sesión"
        binding.buttonGoToLogin.setOnClickListener {
            // Simplemente regresa al fragmento anterior en la pila de retroceso (que debería ser LoginFragment)
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpiar la referencia al binding para evitar fugas de memoria
    }
}