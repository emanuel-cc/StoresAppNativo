package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.editModule.model.EditStoreInteractor

class EditStoreViewModel: ViewModel() {
    // Se declaran propiedades que van a ser observables para la vista
    private val storeSelected = MutableLiveData<StoreEntity>()
    //auxiliar para ocultar el floatingactionbutton
    private val showFab = MutableLiveData<Boolean>()
    //Nos ayuda a determinar si fue actualización o un alta
    private val result = MutableLiveData<Any>()

    private val interactor:EditStoreInteractor

    init {
        interactor = EditStoreInteractor()
    }

    fun setStoreSelected(storeEntity: StoreEntity){
        storeSelected.value = storeEntity
    }
    fun getStoreSelected():LiveData<StoreEntity>{
        return storeSelected
    }

    fun setShowFab(isVisible:Boolean){
        showFab.value = isVisible
    }
    fun getShowFab():LiveData<Boolean>{
        return showFab
    }

    fun setResult(value:Any){
        result.value = value
    }
    fun getResult():LiveData<Any>{
        return result
    }

    fun saveStore(storeEntity: StoreEntity){
        interactor.saveStore(storeEntity, { newId ->
            result.value = newId
        })
    }

    fun updateStore(storeEntity: StoreEntity){
        interactor.updateStore(storeEntity, { storeUpdated ->
            result.value = storeUpdated
        })
    }
}