package com.example.myfood.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myfood.R
import com.example.myfood.database.repositories.EmpresaRepository
import com.example.myfood.databinding.ActivityConfiguracoesEmpresaBinding
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.model.MoreShop
import com.example.myfood.ui.LoadingOverlay
import com.example.myfood.utils.exibirMensagem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ConfiguracoesEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfiguracoesEmpresaBinding
    private val storageReference: StorageReference by lazy { ConfiguracaoFirebase().getFirebaseStorage() }
    private val idUsuarioLogado: String by lazy { ConfiguracaoFirebase().getIdUsuario() }
    private var urlImagemSelecionada = ""
    private lateinit var loading: LoadingOverlay
    private lateinit var launcher: ActivityResultLauncher<String>
    private val empresaRepository = EmpresaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfiguracoesEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loading = LoadingOverlay(findViewById(R.id.loadingOverlay))

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Configurações"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        EditSpinnerCategoria()

        addImageAndUpload()

        binding.buttonAcesso2.setOnClickListener {
            validarDadosEmpresa()
        }

        recuperarDadosEmpresa()

    }

    private fun recuperarDadosEmpresa() {
        loading.showLoading()
        val empresaRef = empresaRepository.recuperarEmpresa()
        empresaRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val empresaValue = dataSnapshot.getValue(MoreShop::class.java)

                    if (empresaValue != null) {
                        binding.editEmpresaNome.setText(empresaValue.text)
                        binding.editEmpresaTaxa.setText(empresaValue.price.toString())
                        binding.editEmpresaTempo.setText(empresaValue.time)
                        val categoriaSpinner = binding.editEmpresaCategoria
                        val categoriaAdapter = categoriaSpinner.adapter as ArrayAdapter<String>
                        val position = categoriaAdapter.getPosition(empresaValue.category)
                        if (position != Spinner.INVALID_POSITION) {
                            categoriaSpinner.setSelection(position)
                        }

                        if (empresaValue.bannerUrl != "") {
                            Picasso.get()
                                .load(empresaValue.bannerUrl)
                                .into(binding.profileImage, object : Callback {
                                    override fun onSuccess() {
                                        loading.hideLoading()
                                    }

                                    override fun onError(e: Exception?) {
                                        loading.hideLoading()
                                    }
                                })
                        } else {
                            loading.hideLoading()
                        }
                    } else {
                        loading.hideLoading()
                        println("Nenhuma empresa associada a este usuário.")
                    }

                } else {
                    loading.hideLoading()
                    println("Nenhuma empresa associada a este usuário.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }

    private fun validarDadosEmpresa() {
        val nome: String = binding.editEmpresaNome.text.toString()
        val taxa: String = binding.editEmpresaTaxa.text.toString()
        val categoria: String = binding.editEmpresaCategoria.selectedItem.toString()
        val tempo: String = binding.editEmpresaTempo.text.toString()

        if (nome.isNotEmpty()) {
            if (taxa.isNotEmpty()) {
                if (categoria.isNotEmpty()) {
                    if (tempo.isNotEmpty()) {
                        salverEmpresa(nome, tempo, categoria, taxa)

                    } else {
                        exibirMensagem("Digite um tempo de entrega", this)
                    }
                } else {
                    exibirMensagem("Digite uma categoria", this)
                }
            } else {
                exibirMensagem("Digite uma taxa de entrega", this)
            }
        } else {
            exibirMensagem("Digite um nome para a empresa", this)
        }

    }

    private fun salverEmpresa(
        nome: String,
        tempo: String,
        categoria: String,
        taxa: String
    ) {
        val empresa =
            MoreShop(
                id = idUsuarioLogado,
                bannerUrl = urlImagemSelecionada,
                text = nome,
                rate = 4.4,
                category = categoria,
                distance = 11.2,
                time = tempo,
                price = taxa.toDouble()
            )

        empresaRepository.salvarEmpresa(
            empresa,
            onSuccess = {
                Log.i("ConfiguracoesEmpresaActivity", "Empresa salva com sucesso")
                finish()
            },
            onFailure = { exception ->
                Log.e("ConfiguracoesEmpresaActivity", "Erro ao salvar Empresa", exception)
            }
        )

    }

    private fun addImageAndUpload() {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.profileImage.setImageURI(uri) // Atualizar local da imagem

                val imageRef = storageReference.child("imagens/empresas/${idUsuarioLogado}.jpeg")

                loading.showLoading()

                imageRef.putFile(uri)
                    .addOnSuccessListener { _ ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            urlImagemSelecionada = uri.toString()
                            exibirMensagem("Sucesso ao fazer upload da imagem", this)
                            loading.hideLoading()
                        }
                    }
                    .addOnFailureListener { exception ->
                        exibirMensagem("Upload da imagem falhou", this)
                        loading.hideLoading()
                    }
            }
        }

        binding.profileImage.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    private fun EditSpinnerCategoria() {
        val spinner = binding.editEmpresaCategoria
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.spiner_categorias)
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Retorna false para desativar o primeiro item
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                // Define a cor do texto para cinza e o tamanho da fonte
                if (position == 0) {
                    textView.setTextColor(Color.GRAY)
                } else {
                    textView.setTextColor(Color.BLACK)
                }
                textView.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    17.5f
                ) // Altere para o tamanho desejado
                textView.letterSpacing = 0.02f
                return view
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                if (position == 0) {
                    // Define a cor do texto para cinza para o primeiro item (desativado)
                    textView.setTextColor(Color.GRAY)
                } else {
                    // Para os outros itens, mantenha o texto preto
                    textView.setTextColor(Color.BLACK)
                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17.5f)
                textView.letterSpacing = 0.02f
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}