// app/java/com/your_package_name/fragments/ProductosFragment.kt
package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager // Para organizar los ítems en lista vertical

import com.example.mitiendaonline.R // Importa la clase R para acceder a los recursos (Drawables, IDs, etc.)
import com.example.mitiendaonline.databinding.FragmentProductosBinding // Importa la clase de binding (FragmentProductosBinding)

// Importa las clases necesarias para el RecyclerView
import com.example.mitiendaonline.data.Producto // Importa la data class Producto
import com.example.mitiendaonline.adapter.ProductoAdapter // Importa el adaptador
import com.example.mitiendaonline.fragments.CarritoFragment // Importa el Fragment del Carrito para navegar

// ProductosFragment hereda de la clase Fragment
class ProductosFragment : Fragment() {

    // Declarar la variable para View Binding
    private var _binding: FragmentProductosBinding? = null

    // Propiedad para acceder al binding de forma segura
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este Fragment usando View Binding
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        val view = binding.root // Obtiene la vista raíz

        // --- Configuración inicial de UI si es necesaria ---

        return view // Devuelve la vista inflada
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Lógica para configurar el RecyclerView y las acciones ---

        // 1. Crear una lista de productos de ejemplo (simulando datos del backend)
        val listaProductosEjemplo = listOf(
            Producto(1, "Smartphone Avanzado", 899.99, R.drawable.placeholder_image), // R.drawable.placeholder_image es el ID de tu drawable
            Producto(2, "Laptop Ultrabook", 1250.00, R.drawable.placeholder_image),
            Producto(3, "Tablet Compacta", 299.50, R.drawable.placeholder_image),
            Producto(4, "Smartwatch Deportivo", 150.00, R.drawable.placeholder_image),
            Producto(5, "Auriculares Inalámbricos", 75.90, R.drawable.placeholder_image),
            Producto(6, "Monitor 4K", 450.70, R.drawable.placeholder_image),
            Producto(7, "Teclado Mecánico", 90.00, R.drawable.placeholder_image),
            Producto(8, "Mouse Ergonómico", 35.00, R.drawable.placeholder_image),
            Producto(9, "Disco SSD 1TB", 110.00, R.drawable.placeholder_image),
            Producto(10, "Router WiFi 6", 85.00, R.drawable.placeholder_image)
            // Puedes añadir más productos de ejemplo aquí
        )
        // NOTA: R.drawable.placeholder_image hace referencia al ID del drawable que creaste.
        // Si le pusiste otro nombre al crear el placeholder, úsalo aquí (ej: R.drawable.mi_placeholder)
        // Idealmente, usarías imágenes reales si las tuvieras, añadiéndolas a drawable y referenciando su ID aquí.

        // 2. Configurar el RecyclerView
        binding.recyclerViewProducts.apply {
            // Configura el LayoutManager. LinearLayoutManager muestra la lista en vertical u horizontal.
            layoutManager = LinearLayoutManager(context) // context es el contexto de la Activity/Fragment

            // Configura el Adaptador. Le pasamos la lista de productos que debe mostrar.
            adapter = ProductoAdapter(listaProductosEjemplo)

            // Opcional: Mejorar rendimiento si el tamaño de los ítems no cambia
            setHasFixedSize(true)
        }

        // 3. Configurar OnClickListener para el ícono del Carrito
        binding.imageViewCartIcon.setOnClickListener {
            // Navegar al CarritoFragment
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CarritoFragment()) // Reemplaza el contenido
                .addToBackStack(null) // Permite volver a la lista de productos
                .commit()
        }

        // Opcional: Implementar click en un ítem de la lista dentro del adaptador
        // Si añadiste click listeners en el ProductoAdapter (Parte 13),
        // la lógica para ir al detalle del producto o añadirlo al carrito al hacer click en el ítem iría DENTRO del adaptador.
    }

    // Limpiar la referencia del binding cuando la vista se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}