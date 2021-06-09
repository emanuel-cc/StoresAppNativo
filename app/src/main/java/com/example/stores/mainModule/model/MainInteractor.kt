package com.example.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

// Se delega la responsabilidad de conseguir los datos
// Abstrae la consulta de datos para devolverlos a quien se los solicite
// en este caso tiene contacto directo con el MainViewModel
class MainInteractor {

    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALLPATH

        var storeList = mutableListOf<StoreEntity>()

        //Se hace la petición con Volley
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            Log.i("Response", response.toString())
            //Se extrae el status de la respuesta
            //val status = response.getInt(Constants.STATUS_PROPERTY)
            // De manera opcional se quiere extraer un entero
            // Si no encuentra el status, se manda el código del error definido
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)
            Log.i("status", status.toString())
            if(status == Constants.SUCCESS){
                Log.i("status", status.toString())

                //Se intenta extraer la lista de tiendas, aunque pueda regresar un null
                val jsonList = response.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                // Se verifica si jsonList es diferente de null
                if(jsonList != null) {
                    // Se define un tipo de dato
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>() {}.type
                    //Se convierte en un listado de objetos de tipo tienda
                    storeList =
                        Gson().fromJson(jsonList, mutableListType)
                    callback(storeList)
                    //Una vez que se regrese el jsonObject, lo demás ya no se ejecuta
                    return@JsonObjectRequest
                }
            }
            callback(storeList)
        },//Permite controlar los errores
            {
            it.printStackTrace()
            callback(storeList)
        })

        StoreApplication.storeApi.addToRequestQueue(jsonObjectRequest)
    }
    // Se crea una función de tipo
    // Se recibe como argumento una función de orden superior
    fun getStoresRoom(callback: (MutableList<StoreEntity>)->Unit){ //Se define entre paréntesis el valor
                                                                // a retornar
        // Se ejecuta el proceso de manera asyncrona
        // Se crea un segundo proceso para traer los datos de la lista
        doAsync {
            val storeList = StoreApplication.database.storeDao().getAllStores()
            // Cuando esté listo los datos se van a setear al adaptador
            uiThread {
                // Se quiere extraer los datos de las tiendas que se tiene en
                // storeList y devolverlo a MainViewModel
                //Se crea una instancia de Gson
                //Convierte una lista en un json
                val json = Gson().toJson(storeList)
                Log.i("Gson", json)
                callback(storeList)
            }
        }
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        // llamamos a las funciones de anko
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}