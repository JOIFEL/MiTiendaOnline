package com.example.mitiendaonline.data.dao

import android.content.ContentValues
import android.content.Context
import com.example.mitiendaonline.data.database.MiTiendaOnline
import com.example.mitiendaonline.data.model.Producto

class daoProducto(context: Context) {
    private val dbHelper = MiTiendaOnline(context)

    // CREATE
    fun insertar(producto: Producto): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("imagenUri", producto.imagenUri)
        }
        return db.insert("tb_productos", null, values)
    }

    // READ
    fun obtenerTodos(): MutableList<Producto> {
        val productos = mutableListOf<Producto>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM tb_productos", null)

        if (cursor.moveToFirst()) {
            do {
                val producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow("imagenUri"))
                )
                productos.add(producto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return productos
    }

    // UPDATE
    fun actualizar(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("imagenUri", producto.imagenUri)
        }
        return db.update("tb_productos", values, "id = ?", arrayOf(producto.id.toString()))
    }

    // DELETE
    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("tb_productos", "id = ?", arrayOf(id.toString()))
    }
}
