package com.example.stores.common.utils

import com.example.stores.common.entities.StoreEntity

interface MainAuxInterface {
    fun hideFab(isVisible:Boolean = false)
    fun addStore(storeEntity: StoreEntity)

    fun updateStore(storeEntity: StoreEntity)
}