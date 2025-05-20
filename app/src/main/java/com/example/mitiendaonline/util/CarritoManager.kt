package com.example.mitiendaonline.util

import android.content.Context
import com.example.mitiendaonline.data.model.CartItem
import com.example.mitiendaonline.data.model.Producto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CarritoManager {
    private const val PREFS_NAME = "carrito_prefs"
    private const val CART_KEY = "carrito"
    private val gson = Gson()


    fun getCarrito(context: Context): MutableList<CartItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(CART_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<CartItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun guardarCarrito(context: Context, carrito: List<CartItem>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(CART_KEY, gson.toJson(carrito)).apply()
    }

    fun agregarAlCarrito(context: Context, producto: Producto) {
        val carrito = getCarrito(context)
        val existente = carrito.find { it.producto.id == producto.id }
        if (existente != null) {
            existente.cantidad++
        } else {
            carrito.add(CartItem(producto, 1))
        }
        guardarCarrito(context, carrito)
    }

    fun eliminarDelCarrito(context: Context, productoId: Int) {
        val carrito = getCarrito(context)
        carrito.removeAll { it.producto.id == productoId }
        guardarCarrito(context, carrito)
    }

    fun limpiarCarrito(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(CART_KEY).apply()
    }
}