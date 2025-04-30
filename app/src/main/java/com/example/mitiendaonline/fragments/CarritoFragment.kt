// app/java/com/your_package_name/fragments/CarritoFragment.kt
package com.example.mitiendaonline.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast // Para mensajes temporales
import androidx.recyclerview.widget.LinearLayoutManager // Para organizar los ítems en lista vertical

import com.example.mitiendaonline.R // Importa la clase R para acceder a los recursos
import com.example.mitiendaonline.databinding.FragmentCarritoBinding // Importa la clase de binding (FragmentCarritoBinding)

// Importa las clases necesarias para el Carrito
import com.example.mitiendaonline.data.Producto // Importa la data class Producto
import com.example.mitiendaonline.data.CartItem // Importa la data class CartItem
import com.example.mitiendaonline.adapter.CarritoAdapter // Importa el adaptador del Carrito

// Importa DecimalFormat para formatear los números con decimales
import java.text.DecimalFormat


// CarritoFragment hereda de la clase Fragment
class CarritoFragment : Fragment() {

    // Declarar la variable para View Binding
    private var _binding: FragmentCarritoBinding? = null

    // Propiedad para acceder al binding de forma segura
    private val binding get() = _binding!!

    // Lista mutable para mantener los ítems del carrito.
    // Usamos var para poder reasignar la lista si es necesario,
    // y MutableList para poder añadir/eliminar ítems dentro.
    private val listaItemsCarrito: MutableList<CartItem> = mutableListOf()

    // Instancia del adaptador del carrito
    private lateinit var carritoAdapter: CarritoAdapter

    // Formateador para mostrar precios con 2 decimales
    private val decimalFormat = DecimalFormat("$#,##0.00")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout para este Fragment usando View Binding
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        val view = binding.root // Obtiene la vista raíz

        // --- Configuración inicial de UI si es necesaria ---

        return view // Devuelve la vista inflada
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Lógica para configurar el RecyclerView, data y acciones ---

        // 1. Crear algunos productos de ejemplo (los mismos que en ProductosFragment)
        // En una app real, obtendrías los productos seleccionados por el usuario.
        val producto1 = Producto(1, "Smartphone Avanzado", 899.99, R.drawable.placeholder_image)
        val producto2 = Producto(2, "Laptop Ultrabook", 1250.00, R.drawable.placeholder_image)
        val producto3 = Producto(3, "Tablet Compacta", 299.50, R.drawable.placeholder_image)
        val producto4 = Producto(4, "Smartwatch Deportivo", 150.00, R.drawable.placeholder_image)
        val producto5 = Producto(5, "Auriculares Inalámbricos", 75.90, R.drawable.placeholder_image)


        // 2. Crear una lista de ítems de carrito de ejemplo (usando los productos de ejemplo)
        listaItemsCarrito.add(CartItem(producto1, 1)) // 1 Smartphone
        listaItemsCarrito.add(CartItem(producto3, 2)) // 2 Tablets
        listaItemsCarrito.add(CartItem(producto5, 3)) // 3 Auriculares
        listaItemsCarrito.add(CartItem(producto2, 1)) // 1 Laptop


        // 3. Definir la lógica para cuando un ítem es eliminado del carrito
        val onItemRemoved: (CartItem) -> Unit = { item ->
            // Esta función se llama desde el adaptador cuando se hace clic en el ícono de eliminar
            listaItemsCarrito.remove(item) // Elimina el ítem de nuestra lista de datos
            carritoAdapter.notifyDataSetChanged() // Notifica al adaptador que los datos cambiaron para que actualice la vista
            updateSummary() // Recalcula y actualiza los totales

            Toast.makeText(requireContext(), "${item.producto.nombre} eliminado", Toast.LENGTH_SHORT).show()
            // Aquí iría lógica para eliminar el ítem también del "backend" o estado global si existiera.
        }

        // 4. Configurar el Adaptador. Le pasamos la lista mutable y el listener para eliminar.
        carritoAdapter = CarritoAdapter(listaItemsCarrito, onItemRemoved)

        // 5. Configurar el RecyclerView
        binding.recyclerViewCartItems.apply {
            // Configura el LayoutManager
            layoutManager = LinearLayoutManager(context)

            // Asigna el Adaptador
            adapter = carritoAdapter

            // Opcional: Mejorar rendimiento
            setHasFixedSize(true)
        }

        // 6. Actualizar el resumen inicial
        updateSummary()

        // 7. Configurar OnClickListener para el botón de Proceder al Pago
        binding.buttonCheckout.setOnClickListener {
            // Aquí iría la lógica real de iniciar el proceso de pago
            if (listaItemsCarrito.isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío.", Toast.LENGTH_SHORT).show()
            } else {
                // Simular inicio de pago
                Toast.makeText(requireContext(), "Procediendo al pago...", Toast.LENGTH_SHORT).show()
                // En una app real, navegarías a una pantalla de pago o iniciarías una transacción.
            }
        }
    }

    // --- Función para calcular y actualizar el resumen de la orden ---
    private fun updateSummary() {
        var subtotal = 0.0
        val taxRate = 0.16 // Ejemplo de tasa de impuestos (16%)
        var tax = 0.0
        var total = 0.0

        // Calcular el subtotal sumando el subtotal de cada ítem en la lista
        for (item in listaItemsCarrito) {
            subtotal += item.subtotal
        }

        // Calcular impuestos y total
        tax = subtotal * taxRate
        total = subtotal + tax

        // Mostrar los valores en los TextViews correspondientes, formateados a 2 decimales
        binding.textViewSubtotal.text = decimalFormat.format(subtotal)
        binding.textViewTax.text = decimalFormat.format(tax)
        binding.textViewTotal.text = decimalFormat.format(total)
    }


    // Limpiar la referencia del binding cuando la vista se destruye
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}