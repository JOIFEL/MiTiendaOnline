package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.data.model.Usuario
import com.example.mitiendaonline.databinding.FragmentRegistroBinding


class RegistroFragment : Fragment() {

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {

            val nombre = binding.editTextFullName.text.toString().trim()
            val correo = binding.editTextEmail.text.toString().trim()
            val contraseña = binding.editTextPassword.text.toString()
            val confirmarContraseña = binding.editTextConfirmPassword.text.toString()

            var isValid = true


            if (nombre.isEmpty()) {
                binding.tilFullName.error = "El nombre es obligatorio"
                isValid = false
            } else {
                binding.tilFullName.error = null
            }


            if (correo.isEmpty()) {
                binding.tilEmail.error = "El correo es obligatorio"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) { // Formato de correo
                binding.tilEmail.error = "Formato de correo no válido"
                isValid = false
            } else {
                binding.tilEmail.error = null
            }


            if (contraseña.isEmpty()) {
                binding.tilPassword.error = "La contraseña es obligatoria"
                isValid = false
            } else if (contraseña.length < 6) { // Ejemplo: mínimo 6 caracteres
                binding.tilPassword.error = "La contraseña debe tener al menos 6 caracteres"
                isValid = false
            } else {
                binding.tilPassword.error = null
            }


            if (confirmarContraseña.isEmpty()) {
                binding.tilConfirmPassword.error = "Confirma la contraseña"
                isValid = false
            } else if (contraseña != confirmarContraseña) {
                binding.tilConfirmPassword.error = "Las contraseñas no coinciden"
                isValid = false
            } else {
                binding.tilConfirmPassword.error = null
            }


            if (isValid) {
                val dao = daoUsuario(requireContext())


                if (correo == "admin@admin.com") {
                    Toast.makeText(requireContext(), "No puedes registrar un usuario con el correo admin por defecto.", Toast.LENGTH_LONG).show()
                    binding.tilEmail.error = "Correo reservado"
                    return@setOnClickListener
                }


                if (dao.existeCorreo(correo)) {
                    binding.tilEmail.error = "Este correo ya está registrado"
                    Toast.makeText(requireContext(), "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    correo = correo,
                    contraseña = contraseña,
                    rol = "cliente",
                    isGoogleUser = false
                )


                val insertadoId = dao.insertar(nuevoUsuario)

                if (insertadoId != -1L) {
                    Toast.makeText(requireContext(), "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al registrar. Intenta de nuevo.", Toast.LENGTH_LONG).show()
                }
            }
        }


        binding.buttonGoToLogin.setOnClickListener {

            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}