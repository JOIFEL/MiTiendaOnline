package com.example.mitiendaonline.fragments

import CarritoFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendaonline.R
import com.example.mitiendaonline.databinding.FragmentProductosBinding
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.adapter.ProductoAdapter
import com.example.mitiendaonline.util.CarritoManager

class ProductosFragment : Fragment() {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = daoProducto(requireContext())
        val productos = dao.obtenerTodos()
        val adapter = ProductoAdapter(productos.toMutableList()) { producto ->
            CarritoManager.agregarAlCarrito(requireContext(), producto)
            Toast.makeText(requireContext(), "${producto.nombre} añadido al carrito.", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewProducts.adapter = adapter
        binding.recyclerViewProducts.setHasFixedSize(true)
        binding.imageViewCartIcon.setOnClickListener {
            // Navegar al CarritoFragment
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CarritoFragment())
                .addToBackStack(null)
                .commit()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}