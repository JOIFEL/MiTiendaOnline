package com.example.mitiendaonline.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.example.mitiendaonline.data.database.MiTiendaOnline
import com.example.mitiendaonline.data.model.Producto

class daoProducto(context: Context) {
    private val dbHelper = MiTiendaOnline(context)

    // --- Métodos CRUD ---

    /**
     * Inserta un nuevo producto en la base de datos.
     * @param producto El objeto Producto a insertar.
     * @return El ID de la fila insertada si es exitoso, -1 de lo contrario.
     */
    fun insertar(producto: Producto): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("imagenUri", producto.imagenUri)
        }
        val newRowId = db.insert("tb_productos", null, values)
        db.close()
        return newRowId
    }

    /**
     * Obtiene todos los productos de la base de datos.
     * @return Una lista mutable de objetos Producto.
     */
    fun obtenerTodos(): MutableList<Producto> {
        val productos = mutableListOf<Producto>()
        val db = dbHelper.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery("SELECT * FROM tb_productos", null)
            if (cursor.moveToFirst()) {
                do {

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                    val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                    val stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))

                    val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow("imagenUri"))

                    val producto = Producto(id, nombre, descripcion, precio, stock, imagenUri)
                    productos.add(producto)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            cursor?.close()
            db.close()
        }
        return productos
    }

    /**
     * Obtiene un producto específico por su ID.
     * @param id El ID del producto a buscar.
     * @return El objeto Producto si se encuentra, o null de lo contrario.
     */
    fun obtenerPorId(id: Int): Producto? {
        val db = dbHelper.readableDatabase
        var producto: Producto? = null
        var cursor: Cursor? = null

        try {

            cursor = db.query(
                "tb_productos",
                null,
                "id = ?",
                arrayOf(id.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                producto = Producto(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                    stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock")),
                    imagenUri = cursor.getString(cursor.getColumnIndexOrThrow("imagenUri"))
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return producto
    }

    /**
     * Actualiza un producto existente en la base de datos.
     * @param producto El objeto Producto con los datos actualizados (el ID se usa para identificarlo).
     * @return El número de filas afectadas, 0 si no se encontró el producto.
     */
    fun actualizar(producto: Producto): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("imagenUri", producto.imagenUri)
        }
        val rowsAffected = db.update("tb_productos", values, "id = ?", arrayOf(producto.id.toString()))
        db.close()
        return rowsAffected
    }

    /**
     * Elimina un producto de la base de datos por su ID.
     * @param id El ID del producto a eliminar.
     * @return El número de filas eliminadas, 0 si no se encontró el producto.
     */
    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("tb_productos", "id = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }
}