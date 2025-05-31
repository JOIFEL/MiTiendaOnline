package com.example.mitiendaonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendaonline.R // ¡Asegúrate de que esta línea esté presente!
import com.example.mitiendaonline.adapter.CarritoAdapter
import com.example.mitiendaonline.data.model.CartItem
import com.example.mitiendaonline.data.model.Producto // ¡Asegúrate de que esta línea esté presente para acceder a producto.id!
import com.example.mitiendaonline.databinding.FragmentCarritoBinding
import com.example.mitiendaonline.util.CarritoManager
import java.text.NumberFormat
import java.util.Locale

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    private val listaItemsCarrito: MutableList<CartItem> = mutableListOf()
    private lateinit var carritoAdapter: CarritoAdapter

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaItemsCarrito.clear()
        listaItemsCarrito.addAll(CarritoManager.getCarrito(requireContext()))

        carritoAdapter = CarritoAdapter(
            listaItemsCarrito,
            onItemRemoved = { item ->
                val index = listaItemsCarrito.indexOf(item)
                if (index != -1) {
                    listaItemsCarrito.removeAt(index)
                    CarritoManager.guardarCarrito(requireContext(), listaItemsCarrito)
                    carritoAdapter.notifyItemRemoved(index)
                    updateSummary()
                    checkEmptyCartState()
                    Toast.makeText(requireContext(), "${item.producto.nombre} eliminado del carrito", Toast.LENGTH_SHORT).show()
                }
            },
            onQuantityIncreased = { item ->
                val index = listaItemsCarrito.indexOf(item)
                if (index != -1) {
                    val currentItem = listaItemsCarrito[index]
                    currentItem.cantidad++
                    CarritoManager.guardarCarrito(requireContext(), listaItemsCarrito)
                    carritoAdapter.notifyItemChanged(index)
                    updateSummary()
                    Toast.makeText(requireContext(), "Cantidad de ${item.producto.nombre} aumentada", Toast.LENGTH_SHORT).show()
                }
            },
            onQuantityDecreased = { item ->
                val index = listaItemsCarrito.indexOf(item)
                if (index != -1) {
                    val currentItem = listaItemsCarrito[index]
                    if (currentItem.cantidad > 1) {
                        currentItem.cantidad--
                        CarritoManager.guardarCarrito(requireContext(), listaItemsCarrito)
                        carritoAdapter.notifyItemChanged(index)
                        updateSummary()
                        Toast.makeText(requireContext(), "Cantidad de ${item.producto.nombre} disminuida", Toast.LENGTH_SHORT).show()
                    } else {
                        val itemToRemove = listaItemsCarrito[index]
                        listaItemsCarrito.removeAt(index)
                        CarritoManager.guardarCarrito(requireContext(), listaItemsCarrito)
                        carritoAdapter.notifyItemRemoved(index)
                        updateSummary()
                        checkEmptyCartState()
                        Toast.makeText(requireContext(), "${itemToRemove.producto.nombre} eliminado del carrito", Toast.LENGTH_SHORT).show()
                    }
                }
            },

            onItemClicked = { item ->

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DetalleProductoFragment.newInstance(item.producto.id))
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.recyclerViewCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carritoAdapter
            setHasFixedSize(true)
        }

        updateSummary()
        checkEmptyCartState()

        binding.buttonCheckout.setOnClickListener {
            if (listaItemsCarrito.isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío. ¡Añade productos para pagar!", Toast.LENGTH_SHORT).show()
            } else {
                // **Navegación a ConfirmarDireccionFragment**
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ConfirmarDireccionFragment()) // Reemplaza al fragmento de ConfirmarDireccion
                    .addToBackStack(null) // Permite regresar al carrito
                    .commit()
                Toast.makeText(requireContext(), "Procediendo al pago...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSummary() {
        var subtotal = 0.0
        val taxRate = 0.19
        for (item in listaItemsCarrito) {
            subtotal += item.subtotal
        }
        val tax = subtotal * taxRate
        val total = subtotal + tax

        binding.textViewSubtotal.text = currencyFormat.format(subtotal)
        binding.textViewTax.text = currencyFormat.format(tax)
        binding.textViewTotal.text = currencyFormat.format(total)
    }

    private fun checkEmptyCartState() {
        if (listaItemsCarrito.isEmpty()) {
            binding.recyclerViewCartItems.visibility = View.GONE
            binding.layoutSummary.visibility = View.GONE
            binding.buttonCheckout.visibility = View.GONE
            binding.textViewEmptyCart.visibility = View.VISIBLE
        } else {
            binding.recyclerViewCartItems.visibility = View.VISIBLE
            binding.layoutSummary.visibility = View.VISIBLE
            binding.buttonCheckout.visibility = View.VISIBLE
            binding.textViewEmptyCart.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}