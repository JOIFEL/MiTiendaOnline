package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.databinding.FragmentLoginBinding
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.mitiendaonline.data.model.Usuario
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.util.UUID
import android.util.Log // Importa Log para depuración

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Etiqueta para Logcat
    private val TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lógica de inicio de sesión con correo/contraseña
        binding.buttonLogin.setOnClickListener {
            val correo = binding.editTextEmailOrUser.text.toString().trim()
            val contraseña = binding.editTextPassword.text.toString()

            // Limpiar errores previos
            binding.tilEmailOrUser.error = null
            binding.tilPassword.error = null

            var isValid = true

            if (correo.isEmpty()) {
                binding.tilEmailOrUser.error = "El correo o usuario es obligatorio"
                isValid = false
            }
            if (contraseña.isEmpty()) {
                binding.tilPassword.error = "La contraseña es obligatoria"
                isValid = false
            }

            if (!isValid) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dao = daoUsuario(requireContext())
            val usuario = dao.getUserByEmail(correo)

            if (usuario == null) {
                binding.tilEmailOrUser.error = "Correo o usuario no registrado"
                Toast.makeText(requireContext(), "Correo o usuario no registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuario.isGoogleUser) {
                Toast.makeText(requireContext(), "Este usuario está registrado con Google. Por favor, usa el botón de Google.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (usuario.contraseña != contraseña) {
                binding.tilPassword.error = "Contraseña incorrecta"
                Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Bienvenido, ${usuario.nombre}!", Toast.LENGTH_SHORT).show()
            navigateToRoleFragment(usuario.rol)
        }

        binding.buttonGoToRegister.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistroFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.buttonGoogleSignIn.setOnClickListener {
            Log.d(TAG, "Botón Iniciar sesión con Google presionado.")
            iniciarSesionConGoogle()
        }
    }

    private fun iniciarSesionConGoogle() {
        val credentialManager = CredentialManager.create(requireContext())
        // ¡¡IMPORTANTE!! Verifica que este Client ID sea de tipo "Android" o "Web" y esté correctamente configurado en Google Cloud Console.
        // Asegúrate de que las huellas SHA-1 (debug y/o release) estén registradas para tu paquete.
        val serverClientId = "770052901333-h1u1luh6o0e8r4cfplbeu7ghg11teth5.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "Intentando obtener credencial de Google...")
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext()
                )
                Log.d(TAG, "Resultado de credencial obtenido.")

                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val displayName = credential.displayName ?: "Usuario Google"
                    val email = credential.id // El ID es el correo electrónico del usuario de Google

                    Log.d(TAG, "Credencial Google ID Token obtenida: Email=$email, DisplayName=$displayName")

                    if (email == null) {
                        Toast.makeText(requireContext(), "No se pudo obtener el correo de Google.", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Correo electrónico de Google es nulo.")
                        return@launch
                    }

                    val dao = daoUsuario(requireContext())
                    val existingUser = dao.getUserByEmail(email)

                    if (existingUser == null) {
                        Log.d(TAG, "Usuario de Google no encontrado en DB, registrando nuevo.")
                        val newUser = Usuario(
                            nombre = displayName,
                            correo = email,
                            contraseña = UUID.randomUUID().toString(),
                            rol = "cliente",
                            isGoogleUser = true
                        )
                        val insertedId = dao.insertar(newUser)
                        if (insertedId != -1L) {
                            Toast.makeText(requireContext(), "Bienvenido, $displayName!", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Nuevo usuario Google registrado con ID: $insertedId")
                            navigateToRoleFragment(newUser.rol)
                        } else {
                            Toast.makeText(requireContext(), "Error al registrar usuario de Google.", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Error al insertar nuevo usuario Google.")
                        }
                    } else {
                        Log.d(TAG, "Usuario de Google encontrado en DB.")
                        if (!existingUser.isGoogleUser) {
                            Log.d(TAG, "Usuario existente no marcado como Google, actualizando...")
                            val updatedUser = existingUser.copy(isGoogleUser = true)
                            val updated = dao.actualizarUsuario(updatedUser)
                            if (updated) {
                                Toast.makeText(requireContext(), "Bienvenido de nuevo, ${existingUser.nombre}!", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "Usuario actualizado a Google user: ${existingUser.nombre}")
                                navigateToRoleFragment(updatedUser.rol)
                            } else {
                                Toast.makeText(requireContext(), "Error al actualizar usuario de Google.", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "Error al actualizar usuario existente a Google user.")
                            }
                        } else {
                            Log.d(TAG, "Usuario ya es un Google user existente.")
                            Toast.makeText(requireContext(), "Bienvenido de nuevo, ${existingUser.nombre}!", Toast.LENGTH_SHORT).show()
                            navigateToRoleFragment(existingUser.rol)
                        }
                    }

                } else {
                    Toast.makeText(requireContext(), "Credencial no válida. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Tipo de credencial no es GoogleIdTokenCredential: $credential")
                }

            } catch (e: GetCredentialException) {
                // Errores específicos del Credential Manager (ej. usuario cancela, no hay conexión, configuración incorrecta)
                Toast.makeText(requireContext(), "Error de autenticación: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "GetCredentialException: ${e.message}", e)
            } catch (e: Exception) {
                // Cualquier otra excepción inesperada
                Toast.makeText(requireContext(), "Ocurrió un error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error inesperado durante Google Sign-In: ${e.message}", e)
            }
        }
    }

    private fun navigateToRoleFragment(rol: String) {
        when (rol) {
            "admin" -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AdminFragment())
                    .commit()
            }
            "cliente" -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProductosFragment())
                    .commit()
            }
            else -> {
                Toast.makeText(requireContext(), "Rol desconocido", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Rol desconocido: $rol")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}