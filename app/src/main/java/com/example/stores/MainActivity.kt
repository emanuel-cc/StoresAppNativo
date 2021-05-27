package com.example.stores

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAuxInterface {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter:StoreAdapter
    private lateinit var mGridLayout:GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main)
        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.editName.text.toString().trim())

            Thread {
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/

        // Se lanza el fragmento
        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }
        setupRecyclerView()
    }

    // Lanza el editstoreFragment
    private fun launchEditFragment(args:Bundle?=null) {
        // Se crea una instancia del fragmento
        val fragment = EditStoreFragment()

        // =====================================================

        // Aquí pasamos argumentos args que le enviaremos el fragmento
        // Preguntamos si args que estamos recibiendo en el método es diferente a null
        // y se lo pasamos al fragment
        if(args != null){
            fragment.arguments = args
        }
        // ======================================================

        /*
        Definiciones:
         - El FragmentManager es el gestor que trae android para controlar los fragmentos
         - El FragmentTransaction es quien va a decidir como es que se va a ejecutar
        */
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Se configura como es que queremos que salga el fragmento
        // Le definimos decirle que fragmento y en donde
        // containerMain es el constraintLayout de activity main
        // fragment es el EditStoreFragment
        fragmentTransaction.add(R.id.containerMain, fragment)
        // Permite regresar a la pantalla anterior
        fragmentTransaction.addToBackStack(null)
        // Se aplican los cambios
        fragmentTransaction.commit()

        // Se oculta el FloatingActionButton
        //mBinding.fab.hide()
        hideFab()
    }

    // Se hace la inicialización de los componentes del recyclerview
    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        // Se establece el tipo de lista, en este caso un grid
        mGridLayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns))

        // Se hace la consulta a la base de datos
        getStores()
        // Se configura el recyclerview
        mBinding.recView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores(){
        // Se ejecuta el proceso de manera asyncrona
        // Se crea un segundo proceso para traer los datos de la lista
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            // Cuando esté listo los datos se van a setear al adaptador
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    /*
    * OnClickListener
    * */
    override fun onClick(storeId: Long) {
        // A este bundle le pasaremos los argumentos
        val args = Bundle()
        //funciona igual que shared preferences o las colecciones tipo map (clave,valor)
        args.putLong(getString(R.string.arg_id), storeId)
        // Le pasamos a la función que lanza el fragmento los argumentos a enviar
        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite

        // llamamos a las funciones de anko
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                updateStore(storeEntity)
            }
        }
    }

    // Sirve para eliminar una tienda de la lista en l base de datos
    override fun onDeleteStore(storeEntity: StoreEntity) {
        // Se obtiene del archivo de values llamado array_options_items
        val items = resources.getStringArray(R.array.array_options_items)

        //Crea un dialogo con multiples opciones
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items, DialogInterface.OnClickListener { dialog, i ->
                when(i){
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> gotoWebSite(storeEntity.website)
                }
            })
            .show()
    }

    // Método que ejecuta un dialog para eliminar una tienda
    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm,
                DialogInterface.OnClickListener { dialog, which ->
                    doAsync {
                        StoreApplication.database.storeDao().deleteStore(storeEntity)
                        uiThread {
                            mAdapter.delete(storeEntity)
                        }
                    }
                })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    // Método que lanza una actividad para marcar a un número telefónico específico
    private fun dial(phone:String){
        // Se crea el intent para disparar la pantalla para marcar al número telefónico
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone") // Se especifica el número a marcar
        }
        // Se lanza la actividad
        // Se hace la validación cuando un dispositivo no es compatible para esa acción
        startIntent(callIntent)
    }

    //Método que lanza el navegador para abrir el sitio web
    private fun gotoWebSite(website:String){
        if(website.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        }else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            // Se hace la validación cuando un dispositivo no es compatible para esa acción
            startIntent(websiteIntent)
        }
    }

    // Se encarga de lanzar un intent dependiendo si es compatible o no con el dispositivo
    private fun startIntent(intent: Intent){
        // Se hace la validación cuando un dispositivo no es compatible para esa acción
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        }else{
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_LONG).show()
        }
    }

    /*
    * MainAuxInterface
    * */
    override fun hideFab(isVisible: Boolean) {
        if(isVisible){
            mBinding.fab.show()
        }else{
            mBinding.fab.hide()
        }
    }

    // Agrega un elemento de la lista para el adaptador
    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    // Actualiza el elemento de la lista para el adaptador
    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}