package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

// Este se comunica con la vista
class MainViewModel :ViewModel(){
    private var storeList:MutableList<StoreEntity>
    private var interactor: MainInteractor

    init {
        // Se pasa a la inicialización
        interactor = MainInteractor()
        storeList = mutableListOf()
    }

    // Se inicializa utilizando lazy
    // Es otra forma de inicializar una variable
    // principalmente variables tipo val
    private val stores:MutableLiveData<List<StoreEntity>> by lazy {
        // Se crea una función de alcance
        MutableLiveData<List<StoreEntity>>().also {
            //Aquí podemos llamar una función en la función de alcance also
            // En este caso estamos consultando los datos
            loadStores()
        }
    }

    // Se obtienen los valores
    // y le devuelve el resultado a la vista
    // Regresa un observable
    fun getStores():LiveData<List<StoreEntity>>{
        return stores
    }

    // Se llenan los datos de la lista o arreglo
    // Tiene contacto directo con el modelo (interactor)
    private fun loadStores(){
        /*interactor.getStoresCallback(object : MainInteractor.StoresCallback{
            override fun getStoresCallback(stores: MutableList<StoreEntity>) {
                this@MainViewModel.stores.value = stores
            }
        })*/
        // Esto se resume a lo que se tiene arriba
        // con la función de tipo
        interactor.getStores {
            stores.value = it
            storeList = it
        }
    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity, {
            val index = storeList.indexOf(storeEntity)

            if(index != -1){
                storeList.removeAt(index)
                // Se actualiza la mutable list data
                stores.value = storeList
            }
        })
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.updateStore(storeEntity, {
            val index = storeList.indexOf(storeEntity)

            if(index != -1){
                storeList.set(index, storeEntity)
                // Se actualiza la mutable list data
                stores.value = storeList
            }
        })
    }
}