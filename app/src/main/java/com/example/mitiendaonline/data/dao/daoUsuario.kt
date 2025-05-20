package com.example.mitiendaonline.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.mitiendaonline.data.database.MiTiendaOnline
import com.example.mitiendaonline.data.model.Producto
import com.example.mitiendaonline.data.model.Usuario

class daoUsuario(private val context: Context) {

  private val dbHelper = MiTiendaOnline(context)

  fun insertar(usuario: Usuario): Boolean {
   val db = dbHelper.writableDatabase
   val valores = ContentValues().apply {
    put("nombre", usuario.nombre)
    put("correo", usuario.correo)
    put("contraseña", usuario.contraseña)
    put("rol",usuario.rol)
   }

   val resultado = db.insert("tb_usuarios", null, valores)
   db.close()
   return resultado != -1L
  }

 fun getUserByEmail(correo: String): Usuario? {
  val db = dbHelper.readableDatabase
  val cursor = db.rawQuery(
   "SELECT * FROM tb_usuarios WHERE correo = ?",
   arrayOf(correo)
  )

  var usuario: Usuario? = null
  if (cursor.moveToFirst()) {
   usuario = Usuario(
    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
    contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña")),
    rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
   )
  }

  cursor.close()
  db.close()
  return usuario
 }


  fun obtenerTodosLosUsuarios(): List<Usuario> {
   val usuarios = mutableListOf<Usuario>()
   val db = dbHelper.readableDatabase
   val cursor = db.rawQuery("SELECT * FROM tb_usuarios", null)

   while (cursor.moveToNext()) {
    val usuario = Usuario(
     id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
     nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
     correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
     contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña")),
     rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
    )
    usuarios.add(usuario)
   }

   cursor.close()
   db.close()
   return usuarios
  }

   fun actualizarUsuario(usuario: Usuario): Boolean {
    if (usuario.correo == "admin@admin.com") return false
     val db = dbHelper.writableDatabase
     val valores = ContentValues().apply {
      put("nombre", usuario.nombre)
      put("correo", usuario.correo)
      put("contraseña", usuario.contraseña)
      put("rol", usuario.rol)
     }

     val resultado = db.update("tb_usuarios", valores, "id = ?", arrayOf(usuario.id.toString()))
     db.close()
     return resultado > 0
   }

 fun existeCorreo(correo: String): Boolean {
  val db = dbHelper.readableDatabase
  val cursor = db.rawQuery("SELECT * FROM tb_usuarios WHERE correo = ?", arrayOf(correo))
  val existe = cursor.moveToFirst()
  cursor.close()
  return existe
 }


  fun eliminarUsuario(id: Int): Boolean {
    val db = dbHelper.writableDatabase
    val resultado = db.delete("tb_usuarios", "id = ?", arrayOf(id.toString()))
    db.close()
    return resultado > 0
  }
}
