package com.example.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.stores.common.database.StoreDatabase

class StoreApplication:Application() {
    // Nos configura el patrón singleton
    // la palabra companion va a ser accesible desde cualquier parte de nuestra aplicación
    // companion es similar a la palabra reservada static
    companion object{
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object:Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                // Alterando la base de datos
                database.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
            }
        }
        // Se construye la base de datos
        database = Room.databaseBuilder(this,
            StoreDatabase::class.java, "StoreDatabse")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}