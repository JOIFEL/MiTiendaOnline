// app/java/com/your_package_name/activities/MainActivity.kt
package com.example.mitiendaonline.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mitiendaonline.R
import com.example.mitiendaonline.databinding.ActivityMainBinding
import com.example.mitiendaonline.fragments.InicioFragment

class MainActivity : AppCompatActivity() {

    // Opcional: Usar View Binding para referenciar vistas más fácilmente (recomendado)
    // private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Si usas View Binding:
        // binding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView(binding.root)
        // Si no usas View Binding, simplemente:
        setContentView(R.layout.activity_main) // Carga el layout que creamos antes

        // Cargar el Fragment inicial solo la primera vez que se crea la Activity
        if (savedInstanceState == null) {
            // Iniciar una transacción de Fragment
            supportFragmentManager.beginTransaction()
                // Reemplazar el contenido del contenedor (fragment_container) con InicioFragment
                // 'addToBackStack(null)' permite volver a este Fragment con el botón atrás
                .replace(R.id.fragment_container, InicioFragment())
                .commit() // Ejecutar la transacción
        }
    }


}