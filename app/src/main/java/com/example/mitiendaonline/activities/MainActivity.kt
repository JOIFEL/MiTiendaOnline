package com.example.mitiendaonline.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mitiendaonline.R // Asume que R.layout.activity_main y R.id.fragment_container son correctos
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.fragments.InicioFragment
import com.example.mitiendaonline.data.model.Usuario
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    // Cliente para obtener la última ubicación conocida
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Crea un lanzador para la solicitud de múltiples permisos
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                // Permisos de ubicación concedidos
                println("DEBUG: Permisos de ubicación concedidos. Intentando obtener la ubicación.")
                getLastKnownLocation() // Intentar obtener la ubicación inmediatamente
            } else {
                // Permisos de ubicación denegados
                println("DEBUG: Permisos de ubicación denegados. La funcionalidad de ubicación no estará disponible.")
                Toast.makeText(this, "La app necesita permisos de ubicación para funcionar.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Llama a la función para crear el admin por defecto
        crearAdminPorDefecto(this)

        // Carga el Fragmento inicial solo si es la primera vez que se crea la actividad
        if (savedInstanceState == null) {
            // Asegúrate de que R.id.fragment_container exista en tu activity_main.xml
            // y que InicioFragment sea el fragmento que quieres cargar al inicio.
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InicioFragment())
                .commit()
        }

        // Verifica y solicita los permisos de ubicación al inicio de la app
        checkAndRequestLocationPermissions()
    }

    // Función para crear el administrador por defecto si no existe
    private fun crearAdminPorDefecto(context: Context) {
        val dao = daoUsuario(context)
        val adminExistente = dao.getUserByEmail("admin@admin.com")

        if (adminExistente == null) {
            val admin = Usuario(
                nombre = "Administrador",
                correo = "admin@admin.com",
                contraseña = "admin123",
                rol = "admin"
            )
            dao.insertar(admin)
            println("DEBUG: Administrador por defecto creado.")
        } else {
            println("DEBUG: Administrador por defecto ya existe.")
        }
    }

    // Función para verificar el estado de los permisos de ubicación y solicitarlos si es necesario
    private fun checkAndRequestLocationPermissions() {
        when {
            // Caso 1: Los permisos ya están concedidos
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                println("DEBUG: Permisos de ubicación ya concedidos. Obteniendo última ubicación conocida.")
                getLastKnownLocation() // Si los permisos ya están, intentar obtener la ubicación
            }
            // Caso 2: Se debe mostrar una explicación al usuario (si ya denegó previamente y no marcó "no volver a preguntar")
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                println("DEBUG: Racional: Se necesita permiso de ubicación para mostrar funcionalidades basadas en tu localización. Por favor, concédelo.")
                // Aquí podrías mostrar un AlertDialog antes de solicitar, pero para simplicidad, solicitamos directamente.
                requestLocationPermissions()
            }
            // Caso 3: Solicitar permisos por primera vez o si el usuario los denegó permanentemente (sin racional)
            else -> {
                println("DEBUG: Solicitando permisos de ubicación por primera vez o después de denegación.")
                requestLocationPermissions()
            }
        }
    }

    // Función que lanza la solicitud de permisos utilizando el lanzador creado
    private fun requestLocationPermissions() {
        val permissionsToRequest = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestLocationPermissionLauncher.launch(permissionsToRequest)
    }

    // Función para obtener la última ubicación conocida
    private fun getLastKnownLocation() {
        // Verificar los permisos nuevamente antes de intentar obtener la ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si por alguna razón los permisos no están, no procedemos.
            println("DEBUG: No hay permisos de ubicación para obtener la última ubicación.")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Hemos obtenido la ubicación. Aquí puedes usarla.
                    val latitude = location.latitude
                    val longitude = location.longitude
                    println("DEBUG: Última ubicación conocida: Latitud=$latitude, Longitud=$longitude")
                    Toast.makeText(this, "Ubicación: Lat $latitude, Lon $longitude", Toast.LENGTH_LONG).show()

                    // Aquí podrías pasar la ubicación a un Fragment, a un ViewModel, o usarla directamente.
                    // Por ejemplo, si tu InicioFragment necesita la ubicación, podrías pasarla así:
                    // val inicioFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? InicioFragment
                    // inicioFragment?.updateLocation(latitude, longitude)

                } else {
                    println("DEBUG: No se pudo obtener la última ubicación conocida. El dispositivo no tiene una ubicación reciente.")
                    Toast.makeText(this, "No se pudo obtener la ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                println("DEBUG: Error al obtener la última ubicación: ${e.message}")
                Toast.makeText(this, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}