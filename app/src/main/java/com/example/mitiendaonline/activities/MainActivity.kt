package com.example.mitiendaonline.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider // Importa ViewModelProvider
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.model.Coordenadas
import com.example.mitiendaonline.data.model.MainViewModel
import com.example.mitiendaonline.fragments.InicioFragment // Ejemplo: si InicioFragment es el primero
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    val ubicacionActual = MutableLiveData<Coordenadas?>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mainViewModel: MainViewModel


    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            Log.d("MainActivity", "Permisos de ubicación concedidos.")
            getLastKnownLocation()
        } else {
            Log.w("MainActivity", "Permisos de ubicación denegados.")
            Toast.makeText(this, "Permisos de ubicación denegados. Algunas funciones no estarán disponibles.", Toast.LENGTH_LONG).show()

            mainViewModel.ubicacionActual.postValue(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InicioFragment())
                .commit()
        }
        checkAndRequestLocationPermissions()
    }

    fun checkAndRequestLocationPermissions() {
        when {

            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("MainActivity", "Permisos de ubicación ya concedidos. Obteniendo última ubicación conocida.")
                getLastKnownLocation()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("MainActivity", "Racional: Se necesita permiso de ubicación. Solicitando.")
                requestLocationPermissions()
            }

            else -> {
                Log.d("MainActivity", "Solicitando permisos de ubicación por primera vez o después de denegación.")
                requestLocationPermissions()
            }
        }
    }


    private fun requestLocationPermissions() {
        val permissionsToRequest = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestLocationPermissionLauncher.launch(permissionsToRequest)
    }


    private fun getLastKnownLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("MainActivity", "getLastKnownLocation: Permisos no concedidos. No se puede obtener la ubicación.")
            mainViewModel.ubicacionActual.postValue(null) // Notificar al ViewModel que la ubicación es nula
            return
        }


        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val coordenadas = Coordenadas(location.latitude, location.longitude)
                    Log.d("MainActivity", "Ubicación obtenida: $coordenadas")

                    mainViewModel.ubicacionActual.postValue(coordenadas)

                } else {
                    Log.w("MainActivity", "No se pudo obtener la última ubicación conocida. El dispositivo no tiene una ubicación reciente.")
                    Toast.makeText(this, "No se pudo obtener la ubicación. Intenta de nuevo.", Toast.LENGTH_SHORT).show()
                    mainViewModel.ubicacionActual.postValue(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error al obtener la última ubicación: ${e.message}", e)
                Toast.makeText(this, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
                mainViewModel.ubicacionActual.postValue(null)
            }
    }
}