// app/java/com/your_package_name/activities/MainActivity.kt
package com.example.mitiendaonline.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoUsuario
import com.example.mitiendaonline.databinding.ActivityMainBinding
import com.example.mitiendaonline.fragments.InicioFragment
import com.example.mitiendaonline.data.model.Usuario

class MainActivity : AppCompatActivity() {

    // Opcional: Usar View Binding para referenciar vistas más fácilmente (recomendado)
    // private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        crearAdminPorDefecto(this)

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                // Reemplazar el contenido del contenedor (fragment_container) con InicioFragment
                // 'addToBackStack(null)' permite volver a este Fragment con el botón atrás
                .replace(R.id.fragment_container, InicioFragment())
                .commit() // Ejecutar la transacción
        }
    }

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
        }
    }
}