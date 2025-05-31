package com.example.mitiendaonline.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.mitiendaonline.data.database.MiTiendaOnline
import com.example.mitiendaonline.data.model.Usuario

class daoUsuario(private val context: Context) {

 private val dbHelper = MiTiendaOnline(context)

 /**
  * Inserta un nuevo usuario en la base de datos.
  * @param usuario El objeto Usuario a insertar.
  * @return true si la inserción fue exitosa, false de lo contrario.
  */
 fun insertar(usuario: Usuario): Long { // Cambia el retorno a Long para el ID de la fila
  val db = dbHelper.writableDatabase
  val valores = ContentValues().apply {
   put("nombre", usuario.nombre)
   put("correo", usuario.correo)
   put("contraseña", usuario.contraseña)
   put("rol", usuario.rol)
   put("isGoogleUser", if (usuario.isGoogleUser) 1 else 0)
  }
  val resultado = db.insert("tb_usuarios", null, valores) // resultado es el row ID (Long)
  db.close()
  return resultado // Devuelve el ID generado
 }


 /**
  * Obtiene un usuario de la base de datos por su correo electrónico.
  * @param correo El correo electrónico del usuario a buscar.
  * @return El objeto Usuario si se encuentra, o null de lo contrario.
  */
 fun getUserByEmail(correo: String): Usuario? {
  val db = dbHelper.readableDatabase
  var cursor: Cursor? = null
  var usuario: Usuario? = null

  try {
   cursor = db.rawQuery(
    "SELECT * FROM tb_usuarios WHERE correo = ?",
    arrayOf(correo)
   )

   if (cursor.moveToFirst()) {
    usuario = Usuario(
     id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
     nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
     correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
     contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña")),
     rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
     isGoogleUser = cursor.getInt(cursor.getColumnIndexOrThrow("isGoogleUser")) == 1 // Lee el campo
    )
   }
  } catch (e: Exception) {
   e.printStackTrace() // Log del error
   // Aquí podrías manejar el error de forma más sofisticada si fuera necesario
  } finally {
   cursor?.close() // Asegura que el cursor se cierre
   db.close() // Asegura que la base de datos se cierre
  }
  return usuario
 }

 /**
  * Obtiene todos los usuarios de la base de datos.
  * @return Una lista de objetos Usuario.
  */
 fun obtenerTodosLosUsuarios(): MutableList<Usuario> {
  val usuarios = mutableListOf<Usuario>()
  val db = dbHelper.readableDatabase
  var cursor: Cursor? = null

  try {
   cursor = db.rawQuery("SELECT * FROM tb_usuarios", null)

   while (cursor.moveToNext()) {
    val usuario = Usuario(
     id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
     nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
     correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
     contraseña = cursor.getString(cursor.getColumnIndexOrThrow("contraseña")),
     rol = cursor.getString(cursor.getColumnIndexOrThrow("rol")),
     isGoogleUser = cursor.getInt(cursor.getColumnIndexOrThrow("isGoogleUser")) == 1 // Lee el campo
    )
    usuarios.add(usuario)
   }
  } catch (e: Exception) {
   e.printStackTrace()
  } finally {
   cursor?.close()
   db.close()
  }
  return usuarios
 }


 /**
  * Actualiza un usuario existente en la base de datos.
  * No permite actualizar el usuario 'admin@admin.com'.
  * @param usuario El objeto Usuario con los datos actualizados (el ID se usa para identificarlo).
  * @return true si la actualización fue exitosa, false de lo contrario.
  */
 fun actualizarUsuario(usuario: Usuario): Boolean {
  // La restricción para 'admin@admin.com' ya está en el UsuarioFragment.
  // Si el usuario.isGoogleUser es true, la edición ya se bloqueó en el Fragment.
  val db = dbHelper.writableDatabase
  val valores = ContentValues().apply {
   put("nombre", usuario.nombre)
   put("correo", usuario.correo)
   put("contraseña", usuario.contraseña)
   put("rol", usuario.rol)
   put("isGoogleUser", if (usuario.isGoogleUser) 1 else 0) // Actualiza el campo
  }

  val resultado = db.update("tb_usuarios", valores, "id = ?", arrayOf(usuario.id.toString()))
  db.close()
  return resultado > 0
 }

 /**
  * Verifica si un correo electrónico ya existe en la base de datos.
  * @param correo El correo electrónico a verificar.
  * @return true si el correo existe, false de lo contrario.
  */
 fun existeCorreo(correo: String): Boolean {
  val db = dbHelper.readableDatabase
  var cursor: Cursor? = null
  var existe = false
  try {
   cursor = db.rawQuery("SELECT * FROM tb_usuarios WHERE correo = ?", arrayOf(correo))
   existe = cursor.moveToFirst()
  } catch (e: Exception) {
   e.printStackTrace()
  } finally {
   cursor?.close()
   db.close()
  }
  return existe
 }

 /**
  * Elimina un usuario de la base de datos por su ID.
  * @param id El ID del usuario a eliminar.
  * @return true si la eliminación fue exitosa, false de lo contrario.
  */
 fun eliminarUsuario(id: Int): Boolean {
  val db = dbHelper.writableDatabase
  val resultado = db.delete("tb_usuarios", "id = ?", arrayOf(id.toString()))
  db.close()
  return resultado > 0
 }
}