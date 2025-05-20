// ...
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mitiendaonline.adapter.CarritoAdapter
import com.example.mitiendaonline.data.model.CartItem
import com.example.mitiendaonline.databinding.FragmentCarritoBinding
import com.example.mitiendaonline.util.CarritoManager
import java.text.DecimalFormat

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!

    private var listaItemsCarrito: MutableList<CartItem> = mutableListOf()
    private lateinit var carritoAdapter: CarritoAdapter
    private val decimalFormat = DecimalFormat("$#,##0.00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listaItemsCarrito = CarritoManager.getCarrito(requireContext())
        carritoAdapter = CarritoAdapter(listaItemsCarrito) { item ->
            // Eliminar item del carrito
            listaItemsCarrito.remove(item)
            CarritoManager.guardarCarrito(requireContext(), listaItemsCarrito)
            carritoAdapter.notifyDataSetChanged()
            updateSummary()
            Toast.makeText(requireContext(), "${item.producto.nombre} eliminado", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = carritoAdapter
            setHasFixedSize(true)
        }

        updateSummary()

        binding.buttonCheckout.setOnClickListener {
            if (listaItemsCarrito.isEmpty()) {
                Toast.makeText(requireContext(), "El carrito está vacío.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Procediendo al pago...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSummary() {
        var subtotal = 0.0
        val taxRate = 0.16
        for (item in listaItemsCarrito) {
            subtotal += item.subtotal
        }
        val tax = subtotal * taxRate
        val total = subtotal + tax

        binding.textViewSubtotal.text = decimalFormat.format(subtotal)
        binding.textViewTax.text = decimalFormat.format(tax)
        binding.textViewTotal.text = decimalFormat.format(total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}