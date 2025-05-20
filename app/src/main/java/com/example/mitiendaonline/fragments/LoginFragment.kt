// app/java/com/your_package_name/fragments/LoginFragment.kt
package com.example.mitiendaonline.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // Podríamos usar esto para mensajes temporales (opcional)

import com.example.mitiendaonline.R // Importa la clase R para acceder a los recursos (IDs, layouts)
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.databinding.FragmentLoginBinding // Importa la clase de binding para este Fragment (FragmentLoginBinding)

// Importa los Fragments a los que queremos navegar
import com.example.mitiendaonline.fragments.ProductosFragment
import com.example.mitiendaonline.fragments.RegistroFragment


// LoginFragment hereda de la clase Fragment
class LoginFragment : Fragment() {

    // Declarar la variable para View Binding, como hicimos en InicioFragment
    private var _binding: FragmentLoginBinding? = null

    // Esta propiedad se usa solo entre onCreateView y onDestroyView
    private val binding get() = _binding!! // Acceso seguro al binding no nulo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este Fragment usando View Binding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root // Obtiene la vista raíz del layout inflado

        // --- Aquí podrías configurar elementos UI que necesitan hacerse al crear la vista ---

        return view // Devuelve la vista inflada
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Lógica para encontrar elementos UI y configurar acciones después de que la vista ha sido creada ---

        // Configurar OnClickListener para el botón de Iniciar Sesión
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


            if (usuario != null && usuario.contraseña == contraseña) {
                Toast.makeText(requireContext(), "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()

                when (usuario.rol) {
                    "admin" -> {
                        // Ir a fragmento de administración
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, AdminFragment())
                            .commit()
                    }
                    "cliente" -> {
                        // Ir a vista de productos
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ProductosFragment())
                            .commit()
                    }
                    else -> {
                        Toast.makeText(requireContext(), "Rol desconocido", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar OnClickListener para el botón "Ir a Registro"
        binding.buttonGoToRegister.setOnClickListener {
            // Navegar al RegistroFragment

            // Obtenemos el FragmentManager
            val fragmentManager = requireActivity().supportFragmentManager

            // Iniciamos una transacción para reemplazar el Fragment actual por RegistroFragment
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegistroFragment()) // Reemplaza el contenido del contenedor
                // No añadimos addToBackStack(null) aquí si queremos que al registrarse y
                // presionar atrás, no vuelva a la pantalla de registro sino al Login (o salir)
                // Sin embargo, para la demo de navegación, sí podemos añadirlo para poder volver
                .addToBackStack(null) // Añade a la pila de atrás para poder volver
                .commit() // Ejecuta la transacción
        }
    }

    // Es importante limpiar la referencia del binding cuando la vista del Fragment se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Establece la referencia del binding a nulo
    }
}