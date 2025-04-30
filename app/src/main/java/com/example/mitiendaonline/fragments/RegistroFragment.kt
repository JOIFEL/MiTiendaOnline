// app/java/com/your_package_name/fragments/RegistroFragment.kt
package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // Para mostrar mensajes temporales

import com.example.mitiendaonline.R // Importa la clase R para acceder a los recursos (IDs, layouts)
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
            // Aquí iría la lógica real de validación y envío de datos de registro (más adelante, con backend)
            // Puedes obtener los datos de los campos así (ejemplo):
            // val nombre = binding.editTextFullName.text.toString()
            // val email = binding.editTextEmail.text.toString()
            // val password = binding.editTextPassword.text.toString()
            // val confirmPassword = binding.editTextConfirmPassword.text.toString()

            // --- Simulación de registro exitoso ---
            // Por ahora, mostramos un mensaje y volvemos a la pantalla de Login

            // Mostrar un mensaje de confirmación temporal
            Toast.makeText(requireContext(), "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

            // Navegar de regreso al LoginFragment.
            // popBackStack() es útil aquí porque simplemente remueve este Fragment (Registro)
            // de la pila de atrás y revela el Fragment anterior (Login).
            requireActivity().supportFragmentManager.popBackStack()

            // Alternativa (menos ideal tras un registro exitoso si Login ya está en la pila):
            // Usar replace también funciona, pero añade una nueva instancia de LoginFragment
            // val fragmentManager = requireActivity().supportFragmentManager
            // fragmentManager.beginTransaction()
            //    .replace(R.id.fragment_container, LoginFragment())
            //    .commit()
        }

        // Configurar OnClickListener para el botón "Ir a Login" (si ya tiene cuenta)
        binding.buttonGoToLogin.setOnClickListener {
            // Navegar de regreso al LoginFragment
            // Aquí podemos usar popBackStack() porque Login está justo debajo
            requireActivity().supportFragmentManager.popBackStack()

            // Alternativa: usar replace (como hicimos al ir de Login a Registro)
            // val fragmentManager = requireActivity().supportFragmentManager
            // fragmentManager.beginTransaction()
            //     .replace(R.id.fragment_container, LoginFragment())
            //     // No añadimos addToBackStack(null) aquí si ya estamos volviendo a un fragment existente
            //     .commit()
        }
    }

    // Limpiar la referencia del binding cuando la vista se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}