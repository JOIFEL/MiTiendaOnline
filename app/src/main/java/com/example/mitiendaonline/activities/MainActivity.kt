// app/java/com/your_package_name/activities/MainActivity.kt
package com.example.mitiendaonline.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mitiendaonline.R
import com.example.mitiendaonline.data.dao.daoUsuario

import com.example.mitiendaonline.fragments.InicioFragment
import com.example.mitiendaonline.data.model.Usuario

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        crearAdminPorDefecto(this)

        if (savedInstanceState == null) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InicioFragment())
                .commit()
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