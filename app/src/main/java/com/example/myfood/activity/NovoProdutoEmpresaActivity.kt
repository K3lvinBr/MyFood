package com.example.myfood.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myfood.R
import com.example.myfood.database.repositories.ProductsRepository
import com.example.myfood.databinding.ActivityNovoProdutoEmpresaBinding
import com.example.myfood.ui.LoadingOverlay
import com.example.myfood.utils.exibirMensagem

class NovoProdutoEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNovoProdutoEmpresaBinding
    private var urlImagemSelecionada = ""
    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var loading: LoadingOverlay
    private val productsRepository = ProductsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovoProdutoEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Novo produto"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loading = LoadingOverlay(findViewById(R.id.loadingOverlay))


        addImage()

        binding.buttonSalvar2.setOnClickListener {
            handleDataNewProduct()
        }
    }

    private fun addImage() {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.editProductImage.setImageURI(uri)
                urlImagemSelecionada = uri.toString()
                exibirMensagem("Imagem selecionada com sucesso", this)
            }
        }

        binding.editProductImage.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    private fun handleDataNewProduct() {
        val name: String = binding.editNomeProduto.text.toString()
        val description: String = binding.editDescricao.text.toString()
        val price: String = binding.editPreco.text.toString()

        if (name.isNotEmpty()) {
            if (description.isNotEmpty()) {

                if (price.isNotEmpty()) {

                    saveProduct(name, description, price.toDouble())

                } else {
                    exibirMensagem("Digite um preço para o produto", this)
                }
            } else {
                exibirMensagem("Digite uma descrição para o produto", this)
            }
        } else {
            exibirMensagem("Digite um nome para o produto", this)
        }

    }

    private fun saveProduct(
        name: String,
        description: String,
        price: Double,
    ) {
        loading.showLoading()

        productsRepository.saveProduct(
            name,
            description,
            price,
            urlImagemSelecionada,
            onSuccess = {
                exibirMensagem("Produto salvo com sucesso", this)
                Log.i("EmpresaActivity", "Produto salvo com sucesso")
                loading.hideLoading()
                finish()
            },
            onFailure = { exception ->
                Log.e("EmpresaActivity", "Erro ao salvar Produto", exception)
                loading.hideLoading()
            }
        )
    }
}