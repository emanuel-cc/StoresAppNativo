package com.example.stores.mainModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

// Se delega la responsabilidad de conseguir los datos
// Abstrae la consulta de datos para devolverlos a quien se los solicite
// en este caso tiene contacto directo con el MainViewModel
class MainInteractor {

    // Se crea una función de tipo
    // Se recibe como argumento una función de orden superior
    fun getStores(callback: (MutableList<StoreEntity>)->Unit){ //Se define entre paréntesis el valor
                                                                // a retornar
        // Se ejecuta el proceso de manera asyncrona
        // Se crea un segundo proceso para traer los datos de la lista
        doAsync {
            val storeList = StoreApplication.database.storeDao().getAllStores()
            // Cuando esté listo los datos se van a setear al adaptador
            uiThread {
                // Se quiere extraer los datos de las tiendas que se tiene en
                // storeList y devolverlo a MainViewModel
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