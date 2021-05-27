package com.example.stores

interface MainAuxInterface {
    fun hideFab(isVisible:Boolean = false)
    fun addStore(storeEntity: StoreEntity)

    fun updateStore(storeEntity: StoreEntity)
}