package com.example.mitiendaonline.fragments

import com.example.mitiendaonline.fragments.CarritoFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendaonline.R
import com.example.mitiendaonline.databinding.FragmentProductosBinding
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.adapter.ProductoAdapter
import com.example.mitiendaonline.data.model.Producto
import com.example.mitiendaonline.util.CarritoManager

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!

    private lateinit var productoAdapter: ProductoAdapter
    private val listaProductos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = daoProducto(requireContext())
        val productos = dao.obtenerTodos()


        productoAdapter = ProductoAdapter(
            productos.toMutableList(),
            onAddToCartClicked = { producto ->
                CarritoManager.agregarAlCarrito(requireContext(), producto)
                updateCartBadge()
                Toast.makeText(requireContext(), "${producto.nombre} añadido al carrito.", Toast.LENGTH_SHORT).show()
            },
            onItemClicked = { producto ->

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DetalleProductoFragment.newInstance(producto.id))
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProducts.adapter = productoAdapter
        binding.recyclerViewProducts.setHasFixedSize(true)

        binding.imageViewCartIcon.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CarritoFragment())
                .addToBackStack(null)
                .commit()
        }

        cargarProductos()
        updateCartBadge()
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

    private fun cargarProductos() {
        val dao = daoProducto(requireContext())
        listaProductos.clear()
        listaProductos.addAll(dao.obtenerTodos())
        productoAdapter.notifyDataSetChanged()
    }

    private fun updateCartBadge() {
        val cartItems = CarritoManager.getCarrito(requireContext())
        val itemCount = cartItems.sumOf { it.cantidad }

        if (itemCount > 0) {
            binding.textViewCartBadge.text = itemCount.toString()
            binding.textViewCartBadge.visibility = View.VISIBLE
        } else {
            binding.textViewCartBadge.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}