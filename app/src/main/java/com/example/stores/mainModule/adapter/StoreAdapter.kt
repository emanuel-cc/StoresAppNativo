package com.example.stores.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ItemStoreBinding
import com.example.stores.mainModule.OnClickListener

class StoreAdapter(private var stores:MutableList<StoreEntity>, private var listener: OnClickListener)
    : RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    private lateinit var mContext: Context // Este es un miembro de la clase

    // Se encarga de inflar la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)

        with(holder){
            binding.txtName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite
            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)
            setListener(store)
        }
    }

    override fun getItemCount(): Int {
        return stores.size
    }


    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    fun add(storeEntity: StoreEntity) {
        if (storeEntity.id != 0L) {
            if(!stores.contains(storeEntity)) {
                stores.add(storeEntity)
                // Le indicamos al adaptador que refresque la vista
                notifyItemInserted(stores.size-1)
            }else{
                update(storeEntity)
            }
        }
    }

    private fun update(storeEntity: StoreEntity) {
        val index = stores.indexOf(storeEntity)

        if(index != -1){
            stores.set(index, storeEntity)
            // Va a refrescar el elemento actualizado
            notifyItemChanged(index)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view)
        fun setListener(storeEntity: StoreEntity){
            with(binding.root) {
                setOnClickListener {
                    listener.onClick(storeEntity)
                }
                setOnLongClickListener {
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }
            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }
        }
    }
}