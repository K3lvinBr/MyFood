package com.example.myfood.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.adapter.EmpresaProductView
import com.example.myfood.database.repositories.ProductsRepository
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.model.Product
import com.example.myfood.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth

class EmpresaActivity : AppCompatActivity() {

    private val autenticacao: FirebaseAuth by lazy { ConfiguracaoFirebase().getFirebaseAutenticacao() }
    private val productsRepository = ProductsRepository()
    private lateinit var productAdapter: EmpresaProductView
    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa)

        // configurando Menu
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Ifood - empresa"
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_produtos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = EmpresaProductView(productList) { position ->

            val alertDialog = AlertDialog.Builder(this).apply {
                setTitle("Deletar produto")
                setMessage("Você tem certeza que deseja deletar esse produto?")
                setPositiveButton("Sim") { _, _ ->
                    val productName = productList[position].name
                    deleteProduct(productName)
                }
                setNegativeButton("Não", null)

            }.show()

            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val redColor = this.resources.getColor(R.color.red, this.theme)
            positiveButton.setTextColor(redColor)
            negativeButton.setTextColor(redColor)
        }
        recyclerView.adapter = productAdapter

        getProducts()
    }

    private fun deleteProduct(
        productName: String
    ) {
        productsRepository.deleteProduct(
            productName,
            onSuccess = {
                exibirMensagem("Produto deletado!", this)
            },
            onFailure = { exception ->
                exibirMensagem("Error ao deletar produto!", this)
                Log.w(ContentValues.TAG, "Error ao deletar produto: ", exception)
            }
        )
    }

    private fun getProducts() {
        productsRepository.fetchProducts(
            productList,
            onSuccess = { productList ->
                productAdapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                Log.w(ContentValues.TAG, "Error getting products: ", exception)
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_empresa, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSair -> {
                deslogarUsuario()
            }

            R.id.menuConfiguracoes -> {
                abrirConfiguracao()
            }

            R.id.menuNovoProduto -> {
                abrirNovoProduto()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deslogarUsuario() {
        try {
            autenticacao.signOut()
            if (!isTaskRoot) {
                finish()
            } else {
                val intent = Intent(this, AutenticacaoActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun abrirConfiguracao() {
        startActivity(Intent(this, ConfiguracoesEmpresaActivity::class.java))
    }

    private fun abrirNovoProduto() {
        startActivity(Intent(this, NovoProdutoEmpresaActivity::class.java))
    }
}