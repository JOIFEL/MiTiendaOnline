package com.example.mitiendaonline.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.mitiendaonline.data.database.MiTiendaOnline
import com.example.mitiendaonline.data.model.Producto

class daoProducto(context: Context) {
    private val dbHelper = MiTiendaOnline(context) // Tu ayudante de base de datos

    // --- Métodos CRUD ---

    /**
     * Inserta un nuevo producto en la base de datos.
     * @param producto El objeto Producto a insertar.
     * @return El ID de la fila insertada si es exitoso, -1 de lo contrario.
     */
    fun insertar(producto: Producto): Long {
        val db = dbHelper.writableDatabase // Obtiene una instancia escribible de la base de datos
        val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("descripcion", producto.descripcion)
            put("precio", producto.precio)
            put("stock", producto.stock)
            put("imagenUri", producto.imagenUri) // 'null' si producto.imagenUri es nulo
        }
        val newRowId = db.insert("tb_productos", null, values) // Inserta los valores
        db.close() // Cierra la base de datos después de la operación
        return newRowId
    }

    /**
     * Obtiene todos los productos de la base de datos.
     * @return Una lista mutable de objetos Producto.
     */
    fun obtenerTodos(): MutableList<Producto> {
        val productos = mutableListOf<Producto>()
        val db = dbHelper.readableDatabase // Obtiene una instancia legible de la base de datos
        var cursor: Cursor? = null // Declara cursor como nullable

        try {
            cursor = db.rawQuery("SELECT * FROM tb_productos", null) // Ejecuta la consulta
            if (cursor.moveToFirst()) { // Mueve el cursor al primer resultado si hay alguno
                do {
                    // Obtiene los valores de las columnas por su nombre y tipo
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                    val descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))
                    val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                    val stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
                    // getString puede devolver null, por lo que Producto.imagenUri debe ser String?
                    val imagenUri = cursor.getString(cursor.getColumnIndexOrThrow("imagenUri"))

                    val producto = Producto(id, nombre, descripcion, precio, stock, imagenUri)
                    productos.add(producto) // Añade el producto a la lista
                } while (cursor.moveToNext()) // Continúa mientras haya más resultados
            }
        } catch (e: Exception) {
            e.printStackTrace() // Imprime la pila de errores para depuración
            // Aquí podrías loggear el error o mostrar un Toast en un contexto apropiado
        } finally {
            cursor?.close() // Asegura que el cursor se cierre siempre
            db.close() // Asegura que la base de datos se cierre siempre
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
            // Consulta para seleccionar un producto por ID
            cursor = db.query(
                "tb_productos",      // Nombre de la tabla
                null,                   // Todas las columnas
                "id = ?",               // Cláusula WHERE
                arrayOf(id.toString()), // Argumentos para la cláusula WHERE
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