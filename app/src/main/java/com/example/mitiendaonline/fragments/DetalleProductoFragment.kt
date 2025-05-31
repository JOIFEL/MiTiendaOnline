package com.example.mitiendaonline.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoProducto
import com.example.mitiendaonline.data.model.Producto
import com.example.mitiendaonline.data.model.CartItem
import com.example.mitiendaonline.databinding.FragmentDetalleProductoBinding
import com.example.mitiendaonline.util.CarritoManager
import java.text.NumberFormat
import java.util.Locale

class DetalleProductoFragment : Fragment() {

    private var _binding: FragmentDetalleProductoBinding? = null
    private val binding get() = _binding!!

    private var productId: Int = -1
    private var currentProduct: Producto? = null
    private var currentCartItem: CartItem? = null

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: Int): DetalleProductoFragment {
            val fragment = DetalleProductoFragment()
            val args = Bundle()
            args.putInt(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getInt(ARG_PRODUCT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProductDetails()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        checkCartStatusAndUpdateUI()
    }

    private fun loadProductDetails() {
        if (productId != -1) {
            val dao = daoProducto(requireContext())
            currentProduct = dao.obtenerPorId(productId)

            currentProduct?.let { product ->
                binding.textViewDetailProductName.text = product.nombre
                binding.textViewDetailProductPrice.text = currencyFormat.format(product.precio)
                binding.textViewDetailProductDescription.text = product.descripcion
                binding.textViewDetailProductStock.text = "Stock disponible: ${product.stock} unidades"

                val uriString = product.imagenUri
                if (uriString != null && uriString.isNotEmpty()) {
                    binding.imageViewDetailProductImage.setImageURI(Uri.parse(uriString))
                } else {
                    binding.imageViewDetailProductImage.setImageResource(R.drawable.ic_image_placeholder)
                }

                checkCartStatusAndUpdateUI()
            } ?: run {
                Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        } else {
            Toast.makeText(requireContext(), "ID de producto no proporcionado", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupListeners() {
        binding.buttonAddToCartDetail.setOnClickListener {
            currentProduct?.let { product ->
                CarritoManager.agregarAlCarrito(requireContext(), product)
                Toast.makeText(requireContext(), "${product.nombre} añadido al carrito.", Toast.LENGTH_SHORT).show()
                checkCartStatusAndUpdateUI()
            }
        }

        binding.buttonDetailPlus.setOnClickListener {
            currentProduct?.let { product ->
                val carrito = CarritoManager.getCarrito(requireContext())
                val item = carrito.find { it.producto.id == product.id }
                item?.let {
                    if (product.stock > 0 && it.cantidad < product.stock) {
                        it.cantidad++
                        CarritoManager.guardarCarrito(requireContext(), carrito)
                        binding.textViewDetailQuantity.text = it.cantidad.toString()
                        Toast.makeText(requireContext(), "Cantidad de ${product.nombre} aumentada a ${it.cantidad}", Toast.LENGTH_SHORT).show()
                    } else if (it.cantidad >= product.stock && product.stock > 0) {
                        Toast.makeText(requireContext(), "Has alcanzado el stock máximo disponible.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Producto sin stock disponible.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.buttonDetailMinus.setOnClickListener {
            currentProduct?.let { product ->
                val carrito = CarritoManager.getCarrito(requireContext())
                val item = carrito.find { it.producto.id == product.id }
                item?.let {
                    if (it.cantidad > 1) {
                        it.cantidad--
                        CarritoManager.guardarCarrito(requireContext(), carrito)
                        binding.textViewDetailQuantity.text = it.cantidad.toString()
                        Toast.makeText(requireContext(), "Cantidad de ${product.nombre} disminuida a ${it.cantidad}", Toast.LENGTH_SHORT).show()
                    } else {
                        CarritoManager.eliminarDelCarrito(requireContext(), product.id)
                        Toast.makeText(requireContext(), "${product.nombre} eliminado del carrito", Toast.LENGTH_SHORT).show()
                        checkCartStatusAndUpdateUI()
                    }
                }
            }
        }
    }

    private fun checkCartStatusAndUpdateUI() {
        currentProduct?.let { product ->
            val carrito = CarritoManager.getCarrito(requireContext())
            currentCartItem = carrito.find { it.producto.id == product.id }

            if (currentCartItem != null) {
                binding.buttonAddToCartDetail.visibility = View.GONE
                binding.layoutDetailQuantitySelector.visibility = View.VISIBLE
                binding.textViewDetailQuantity.text = currentCartItem!!.cantidad.toString()
            } else {
                binding.buttonAddToCartDetail.visibility = View.VISIBLE
                binding.layoutDetailQuantitySelector.visibility = View.GONE
            }

            if (product.stock <= 0) {
                binding.buttonAddToCartDetail.isEnabled = false
                binding.buttonDetailPlus.isEnabled = false
                binding.buttonDetailMinus.isEnabled = currentCartItem != null
                Toast.makeText(requireContext(), "Producto sin stock disponible.", Toast.LENGTH_LONG).show()
            } else {
                binding.buttonAddToCartDetail.isEnabled = true
                binding.buttonDetailPlus.isEnabled = true
                binding.buttonDetailMinus.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
