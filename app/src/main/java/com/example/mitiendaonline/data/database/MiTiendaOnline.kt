package com.example.mitiendaonline.data.database


import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MiTiendaOnline(context: Context) : SQLiteOpenHelper(context, DATABASE_NOMBRE, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "MiTiendaOnline"
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NOMBRE = "mi_tienda_online.db"
        private const val TABLE_USUARIOS = "tb_usuarios"
        private const val TABLE_PRODUCTOS = "tb_productos"


        private const val SQL_CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                contraseña TEXT NOT NULL,
                rol TEXT NOT NULL,
                isGoogleUser INTEGER DEFAULT 0 
            )
        """


        private const val SQL_CREATE_TABLE_PRODUCTOS = """
            CREATE TABLE $TABLE_PRODUCTOS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT,
                precio REAL NOT NULL,
                stock INTEGER NOT NULL,
                imagenUri TEXT 
            )
        """

        private const val SQL_INSERT_DEFAULT_ADMIN = """
            INSERT INTO $TABLE_USUARIOS (nombre, correo, contraseña, rol, isGoogleUser)
            VALUES ('Admin', 'admin@admin.com', 'admin123', 'admin', 0)
        """
    }



    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creando tablas de la base de datos, versión: $DATABASE_VERSION")
        db.execSQL(SQL_CREATE_TABLE_USUARIOS)
        db.execSQL(SQL_CREATE_TABLE_PRODUCTOS)
        db.execSQL(SQL_INSERT_DEFAULT_ADMIN)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Actualizando base de datos de versión $oldVersion a $newVersion")
        if (oldVersion < 2) {
            val alterTableUsers = "ALTER TABLE $TABLE_USUARIOS ADD COLUMN isGoogleUser INTEGER DEFAULT 0"
            db.execSQL(alterTableUsers)
            Log.d(TAG, "Columna 'isGoogleUser' añadida a $TABLE_USUARIOS")

        }
    }


    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(TAG, "Downgrading database from version $oldVersion to $newVersion. Data will be lost!")

        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        onCreate(db)
    }
}