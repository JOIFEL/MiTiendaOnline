// app/java/com/your_package_name/fragments/InicioFragment.kt
package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mitiendaonline.R // Importa la clase R para acceder a los recursos (IDs, layouts)
import com.example.mitiendaonline.databinding.FragmentInicioBinding // Importa la clase de binding para este Fragment (FragmentInicioBinding)
// Importa el LoginFragment al que queremos navegar
import com.example.mitiendaonline.fragments.LoginFragment

// InicioFragment hereda de la clase Fragment
class InicioFragment : Fragment() {

    // Declarar la variable para View Binding
    // El '?' indica que puede ser nulo inicialmente
    private var _binding: FragmentInicioBinding? = null

    // Esta propiedad se usa solo entre onCreateView y onDestroyView
    // para asegurar que no accedemos al binding cuando la vista ya no existe
    private val binding get() = _binding!! // El '!!' asegura que accedemos a un valor no nulo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este Fragment usando View Binding
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val view = binding.root // Obtiene la vista raíz del layout inflado

        // --- Aquí podrías configurar elementos UI que necesitan hacerse al crear la vista ---
        // Por ahora, la lógica del botón la ponemos en onViewCreated

        return view // Devuelve la vista inflada
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Lógica para encontrar elementos UI y configurar acciones después de que la vista ha sido creada ---

        // Encontrar el botón "Comenzar" usando View Binding y configurar su OnClickListener
        binding.buttonStart.setOnClickListener {
            // Navegar al LoginFragment

            // Obtiene el FragmentManager de la Activity que contiene este Fragment
            val fragmentManager = requireActivity().supportFragmentManager

            // Inicia una transacción de Fragment
            fragmentManager.beginTransaction()
                // Reemplaza el Fragment actual (el que está en el contenedor)
                // por una nueva instancia de LoginFragment
                .replace(R.id.fragment_container, LoginFragment())
                // Añade esta transacción a la "back stack" (pila de atrás).
                // Esto permite que el usuario use el botón atrás del dispositivo
                // para volver del LoginFragment a este InicioFragment.
                .addToBackStack(null)
                // Ejecuta la transacción
                .commit()
        }
    }

    // Es importante limpiar la referencia del binding cuando la vista del Fragment se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Establece la referencia del binding a nulo
    }
}