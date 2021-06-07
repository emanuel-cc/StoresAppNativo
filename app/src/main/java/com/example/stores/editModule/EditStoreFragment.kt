package com.example.stores.editModule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentEditStoreBinding
    //MVVM
    private lateinit var mEditStoreViewModel:EditStoreViewModel

    private var mActivity: MainActivity? = null

    // Se distingue si vamos a editar o a agregar
    private var mIsEditMode:Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Se inicializa el viewmodel
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Lo vincula con el layout fragment
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_edit_store, container, false)

        return mBinding.root
    }

    // Cuando la vista se ha creado por completo
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //MVVM
        setupViewModel()

        // Se llama la función de la configuración de los textfields
        setupTextFields()
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner, {
            //Se obtiene el nuevo valor
            mStoreEntity = it
            // Se pregunta si el id es diferente de null y de 0
            if(it.id != 0L){
                //Toast.makeText(activity, id.toString(), Toast.LENGTH_SHORT).show()
                mIsEditMode = true
                //Contendrá el elemento seleccionado
                setUiStore(it)
            }else{
                mIsEditMode = false
            }

            //Se llama al método para configurar el actionbar
            setupActionBar()
        })

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner, { result ->
            hideKeyboard()
            when(result){
                //Cuando es un nuevo registro
                is Long ->{
                    // Se reasigna el id
                    mStoreEntity.id = result
                    // Se notifica que hay una nueva tienda
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    // TODO: 07/06/2021
                    //mActivity?.addStore(mStoreEntity!!)
                    Toast.makeText(
                        mActivity,
                        R.string.edit_store_message_save_success,
                        Toast.LENGTH_SHORT
                    ).show()
                    mActivity?.onBackPressed()
                }
                //Cuando se está actualizando
                is StoreEntity->{
                    // Se notifica que hay una tienda actualizada
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)

                    // Aquí actualizamos los datos de la tienda cuando estamos en modo
                    // edit
                    // TODO: 07/06/2021  viewmodel
                    //mActivity?.updateStore(mStoreEntity!!)
                    Snackbar.make(mBinding.root,
                        R.string.edit_store_message_update_success,
                        Snackbar.LENGTH_SHORT).show()
                }
            }
            // Almacena los datos de la tienda, para almacenarla o editarlo
            /*Snackbar.make(mBinding.root, getString(R.string.edit_store_message_save_success),
            Snackbar.LENGTH_SHORT).show()*/

        })
    }

    //Método que sirve para configurar el actionbar
    private fun setupActionBar() {
        /*
            Sirve para configurar la barra de acciones
        */
        //============================================================
        // Se consigue la actividad la cual esta alojada el fragmento
        mActivity = activity as? MainActivity
        // Le indica que muestre una flecha de retroceso en la parte de arriba
        // de la barra superior de la pantalla
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Le establecemos un título en la barra superior del fragmento
        mActivity?.supportActionBar?.title =
            if(mIsEditMode) getString(R.string.edit_store_title_edit)
            else getString(R.string.edit_store_title_add)

        // Tener acceso al menu
        setHasOptionsMenu(true)
        //============================================================
    }

    // Función para configurar a los textfields edittext
    private fun setupTextFields() {
        with(mBinding) {
            // Se agrega un textchangelistener a los edittext
            editName.addTextChangedListener {
                validateFields(tilName)
            }
            editPhone.addTextChangedListener {
                validateFields(tilPhone)
            }
            // Agregamos la url de la foto del campo de url imagen a la imageview
            // para vista previa
            editPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url:String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    // Permite obtener los datos de una tienda
    /*private fun getStore(id: Long) {
        // Usamos anko para peticiones asíncronas y en segundo plano
        doAsync {
            mStoreEntity = StoreApplication.database.storeDao().getStoreById(id)
            uiThread {
                // Se empieza a llenar los campos del formulario del fragmento
                // ya que termine la ejecución de la consulta de los datos
                if(mStoreEntity != null){
                    // Se llama a la función que permite llenar la información a los elementos de
                    // la interfaz del fragmento
                    setUiStore(mStoreEntity!!)
                }
            }
        }
    }*/

    // Permite asignar los datos a los elementos de la interfaz del fragmento
    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding){
            // Otra forma de asignar un valor a un campo de texto tipo editText
            editName.text = storeEntity.name.editable()
            editPhone.text = storeEntity.phone.editable()
            editWebSite.text = storeEntity.website.editable()
            editPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    //Una función extensión que regresa un editable que permite asignar un valor text a un edittext
    private fun String.editable():Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Le pasamos el menu creado, que aparecerá en la parte derecha de la barra superior
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when(item.itemId){
            android.R.id.home -> {
                // Similar cuando se presiona el boton atras del dispositivo
                mActivity?.onBackPressed()
                true
            }
            // El que tiene el ícono del check
            R.id.action_save -> {
                // Valida si el objeto storeentity es diferente de null y que no esté vacío
               if(validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)){
                   /* val store = StoreEntity(name = mBinding.editName.text.toString().trim(),
                    phone = mBinding.editPhone.text.toString(),
                    website = mBinding.editWebSite.text.toString(),
                    photoUrl = mBinding.editPhotoUrl.text.toString().trim())*/

                   with(mStoreEntity){
                       name = mBinding.editName.text.toString().trim()
                       phone = mBinding.editPhone.text.toString()
                       website = mBinding.editWebSite.text.toString()
                       photoUrl = mBinding.editPhotoUrl.text.toString().trim()
                   }

                   // Se pregunta si está en modo editable o modo para agregar elemento
                   if(mIsEditMode){
                       //StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                       mEditStoreViewModel.updateStore(mStoreEntity)
                   }else{
                       //mStoreEntity!!.id = StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                        mEditStoreViewModel.saveStore(mStoreEntity)
                   }
               }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    // Permite validar los campos del formulario, indicar cuales son requeridos
    // Versión corta
    private fun validateFields(vararg textFields:TextInputLayout):Boolean{
        var isvalid = true
        for(textfield in textFields){
            if(textfield.editText?.text.toString().trim().isEmpty()){
                textfield.error = getString(R.string.helper_required)
                //textfield.editText?.requestFocus()
                isvalid = false
            }else{
                textfield.error = null
            }
        }

        // Valida si algún campo está vacío, para que lanze un snackbar
        if(!isvalid){
            Snackbar.make(mBinding.root,
                R.string.edit_store_message_valid, Snackbar.LENGTH_SHORT).show()
        }
        return isvalid
    }

    // Permite validar los campos del formulario, indicar cuales son requeridos
    private fun validateFields(): Boolean {
        var isvalid = true

        if(mBinding.editPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            // Le asigna el foco al campo que es requerido
            mBinding.editPhotoUrl.requestFocus()
            isvalid = false
        }
        if(mBinding.editPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            // Le asigna el foco al campo que es requerido
            mBinding.editPhone.requestFocus()
            isvalid = false
        }
        if(mBinding.editName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            // Le asigna el foco al campo que es requerido
            mBinding.editName.requestFocus()
            isvalid = false
        }
        return isvalid
    }

    // Permite ocultar el teclado del teléfono
    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(view != null){
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        // Se oculta la barra superior del fragmento
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        // Mostramos en la pantalla principal el FloatingActionButton
        mEditStoreViewModel.setShowFab(true)

        //Limpiar resultados almacenados en el ViewModel
        mEditStoreViewModel.setResult(Any())
        // Se encarga de desvincularlo de la pantalla principal
        setHasOptionsMenu(false)
        super.onDestroy()
    }
}