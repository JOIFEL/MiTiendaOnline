// app/java/com/your_package_name/fragments/LoginFragment.kt
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
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val correo = binding.editTextEmailOrUser.text.toString().trim()
            val contraseña = binding.editTextPassword.text.toString()

            if (correo.isEmpty() || contraseña.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dao = daoUsuario(requireContext())
            val usuario = dao.getUserByEmail(correo)

            if (usuario == null) {
                Toast.makeText(requireContext(), "El correo no está registrado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuario.contraseña != contraseña) {
                Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "Bienvenido, ${usuario.nombre}!", Toast.LENGTH_SHORT).show()

            when (usuario.rol) {
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
                }
            }
        }

        binding.buttonGoToRegister.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistroFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.buttonGoogleSignIn.setOnClickListener {
            iniciarSesionConGoogle()
        }
    }

    private fun iniciarSesionConGoogle() {
        val credentialManager = CredentialManager.create(requireContext())

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("770052901333-h1u1luh6o0e8r4cfplbeu7ghg11teth5.apps.googleusercontent.com")
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = requireContext()
                )

                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val displayName = credential.displayName
                    val email = credential.id

                    Toast.makeText(requireContext(), "Bienvenido $displayName", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProductosFragment())
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Credencial no válida", Toast.LENGTH_SHORT).show()
                }

            } catch (e: GetCredentialException) {
                Toast.makeText(requireContext(), "Error de autenticación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
