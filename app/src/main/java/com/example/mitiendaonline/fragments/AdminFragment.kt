package com.example.mitiendaonline.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mitiendaonline.R
import com.example.mitiendaonline.adapter.DashboardAdapter
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.data.model.Producto

class AdminFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addProductButton: Button
    private lateinit var addAdminButton: Button

    private val listaProductos = mutableListOf<Producto>()
    private lateinit var adapter: DashboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerViewAdminProducts)
        addProductButton = view.findViewById(R.id.buttonAddProduct)
        addAdminButton = view.findViewById(R.id.buttonAddAdmin)

        adapter = DashboardAdapter(listaProductos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addProductButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AgregarProductosFragment())
                .addToBackStack(null)
                .commit()
        }

        addAdminButton.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UsuarioFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }

    private fun cargarProductos() {
        listaProductos.clear()
        listaProductos.addAll(daoProducto(requireContext()).obtenerTodos())
        adapter.notifyDataSetChanged()
    }
}