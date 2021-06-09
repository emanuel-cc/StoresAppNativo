package com.example.stores.common.database

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class StoreApi constructor(context:Context) {
    //Parecido a la clase estática
    companion object{
        @Volatile
        private var INSTANCE:StoreApi? = null

        fun getInstance(context: Context) = INSTANCE?: synchronized(this){
            // Se va a instanciar una vez
            INSTANCE?:StoreApi(context).also {
                INSTANCE = it
            }
        }
    }

    //Administra las operaciones de red de forma asyncrona
    // Además de leer y escribir en la caché
    val requestQueue:RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>){
        requestQueue.add(req)
    }
}