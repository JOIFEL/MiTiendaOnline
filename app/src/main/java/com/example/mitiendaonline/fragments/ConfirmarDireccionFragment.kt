package com.example.mitiendaonline.fragments

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mitiendaonline.activities.MainActivity
import com.example.mitiendaonline.data.model.Coordenadas
import com.example.mitiendaonline.data.model.MainViewModel
import com.example.mitiendaonline.databinding.FragmentConfirmarDireccionBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class ConfirmarDireccionFragment : Fragment() {

    private companion object {
        private const val TAG = "ConfirmarDireccion"
    }

    private var _binding: FragmentConfirmarDireccionBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private var geocodingJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmarDireccionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupListeners()
        setupObservers()

        mainViewModel.ubicacionActual.value?.let { currentCoords ->
            if (areAddressFieldsEmpty()) {
                Log.d(TAG, "onViewCreated: Initial coordinates present. Attempting geocoding.")
                convertirCoordenadasADireccion(currentCoords)
            }
        } ?: run {
            Log.d(TAG, "onViewCreated: No initial coordinates in ViewModel.")
            if (binding.progressBarDireccion.visibility == View.VISIBLE) {
                setUiLoadingState(false)
            }
        }
    }

    private fun areAddressFieldsEmpty(): Boolean {
        return binding.etCalle.text.isNullOrEmpty() &&
                binding.etCiudad.text.isNullOrEmpty() &&
                binding.etEstadoProvincia.text.isNullOrEmpty() &&
                binding.etCodigoPostal.text.isNullOrEmpty() &&
                binding.etPais.text.isNullOrEmpty()
    }

    private fun setupListeners() {
        binding.btnUsarUbicacionActual.setOnClickListener {
            geocodingJob?.cancel()
            setUiLoadingState(true)
            (activity as? MainActivity)?.checkAndRequestLocationPermissions()
        }

        binding.btnConfirmarDireccion.setOnClickListener {
            if (validateInputs()) {
                val calle = binding.etCalle.text.toString().trim()
                val ciudad = binding.etCiudad.text.toString().trim()
                val estado = binding.etEstadoProvincia.text.toString().trim()
                val cp = binding.etCodigoPostal.text.toString().trim()
                val pais = binding.etPais.text.toString().trim()

                val direccionCompleta = "$calle, $ciudad, $estado, $cp, $pais".trim(',',' ')
                Toast.makeText(requireContext(), "Dirección Confirmada: $direccionCompleta", Toast.LENGTH_LONG).show()
                Log.i(TAG, "Dirección Confirmada: $direccionCompleta")

                // TODO: Implement logic to save the address or navigate
            } else {
                Toast.makeText(requireContext(), "Por favor, completa los campos requeridos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        mainViewModel.ubicacionActual.observe(viewLifecycleOwner) { coordenadas ->
            coordenadas?.let {
                Log.d(TAG, "Observer: Received coordinates from ViewModel: $it")
                val explicitlyTriggered = binding.progressBarDireccion.visibility == View.VISIBLE
                if (explicitlyTriggered || areAddressFieldsEmpty()) {
                    convertirCoordenadasADireccion(it)
                } else {
                    Log.d(TAG, "Observer: Coordinates received, but UI suggests not to auto-fill.")
                }
            } ?: run {
                Log.d(TAG, "Observer: Coordinates are null in ViewModel.")
                if (binding.progressBarDireccion.visibility == View.VISIBLE) {
                    setUiLoadingState(false)
                }
            }
        }
    }

    private fun convertirCoordenadasADireccion(coordenadas: Coordenadas) {
        if (!isAdded || context == null || _binding == null) {
            Log.w(TAG, "convertirCoordenadasADireccion called but fragment not in a valid state.")
            setUiLoadingState(false)
            return
        }

        setUiLoadingState(true)
        limpiarCampos()

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        geocodingJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                Log.d(TAG, "Geocoding: Lat=${coordenadas.latitud}, Lon=${coordenadas.longitud}")

                val addresses: List<Address>? = withContext(Dispatchers.IO) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            geocoder.getFromLocation(coordenadas.latitud, coordenadas.longitud, 1)
                        } else {
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocation(coordenadas.latitud, coordenadas.longitud, 1)
                        }
                    } catch (e: IOException) {
                        Log.e(TAG, "Geocoder.getFromLocation IO Exception: ${e.message}", e)
                        null
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG, "Geocoder.getFromLocation Invalid Coordinates: ${e.message}", e)
                        null
                    }
                }

                if (!isAdded || _binding == null) return@launch

                setUiLoadingState(false)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    Log.d(TAG, "Address found: $address")

                    val thoroughfare = address.thoroughfare
                    val subThoroughfare = address.subThoroughfare

                    val streetAddress = when {
                        !thoroughfare.isNullOrEmpty() && !subThoroughfare.isNullOrEmpty() -> "$thoroughfare $subThoroughfare"
                        !thoroughfare.isNullOrEmpty() -> thoroughfare
                        else -> ""
                    }

                    binding.etCalle.setText(streetAddress)
                    binding.etCiudad.setText(address.locality ?: "")
                    binding.etEstadoProvincia.setText(address.adminArea ?: "")
                    binding.etCodigoPostal.setText(address.postalCode ?: "")
                    binding.etPais.setText(address.countryName ?: "")
                    Toast.makeText(requireContext(), "Dirección sugerida. Por favor, verifica.", Toast.LENGTH_LONG).show()
                } else {
                    Log.w(TAG, "No address found for the coordinates.")
                    Toast.makeText(requireContext(), "No se pudo encontrar una dirección. Ingresa manualmente.", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                if (isAdded && _binding != null) setUiLoadingState(false)
                Log.e(TAG, "Unexpected error during geocoding: ${e.message}", e)
                Toast.makeText(requireContext(), "Ocurrió un error al obtener la dirección.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        binding.tilCalle.error = if (binding.etCalle.text.toString().trim().isEmpty()) {
            isValid = false
            "La calle es obligatoria"
        } else null

        binding.tilCiudad.error = if (binding.etCiudad.text.toString().trim().isEmpty()) {
            isValid = false
            "La ciudad es obligatoria"
        } else null

        binding.tilPais.error = if (binding.etPais.text.toString().trim().isEmpty()) {
            isValid = false
            "El país es obligatorio"
        } else null

        return isValid
    }

    private fun limpiarCampos() {
        binding.etCalle.setText("")
        binding.etCiudad.setText("")
        binding.etEstadoProvincia.setText("")
        binding.etCodigoPostal.setText("")
        binding.etPais.setText("")

        binding.tilCalle.error = null
        binding.tilCiudad.error = null
        binding.tilEstadoProvincia.error = null
        binding.tilCodigoPostal.error = null
        binding.tilPais.error = null
    }

    private fun setUiLoadingState(isLoading: Boolean) {
        _binding?.apply {
            progressBarDireccion.visibility = if (isLoading) View.VISIBLE else View.GONE

            val isEnabled = !isLoading
            tilCalle.isEnabled = isEnabled
            tilCiudad.isEnabled = isEnabled
            tilEstadoProvincia.isEnabled = isEnabled
            tilCodigoPostal.isEnabled = isEnabled
            tilPais.isEnabled = isEnabled
            btnConfirmarDireccion.isEnabled = isEnabled
            btnUsarUbicacionActual.isEnabled = isEnabled
        }
    }

    override fun onStop() {
        super.onStop()
        geocodingJob?.cancel()
        if (binding.progressBarDireccion.visibility == View.VISIBLE) {
            setUiLoadingState(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        geocodingJob?.cancel()
        _binding = null
    }
}