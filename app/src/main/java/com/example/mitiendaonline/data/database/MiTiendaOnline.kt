package com.example.mitiendaonline.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class MiTiendaOnline(context: Context) : SQLiteOpenHelper(context, DATABASE_NOMBRE, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NOMBRE = "mi_tienda_online.db"
        private const val TABLE_USUARIOS = "tb_usuarios"
        private const val TABLE_PRODUCTOS = "tb_productos"

        private const val SQL_CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                contraseña TEXT NOT NULL,
                rol TEXT NOT NULL 
            )
        """

        private const val SQL_CREATE_TABLE_PRODUCTOS = """
            CREATE TABLE $TABLE_PRODUCTOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                stock INTEGER NOT NULL,
                imagenUri TEXT NOT NULL
            )
        """


    }


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_TABLE_USUARIOS)
        db?.execSQL(SQL_CREATE_TABLE_PRODUCTOS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        onCreate(db)
    }

}
