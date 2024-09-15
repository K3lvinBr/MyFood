package com.example.myfood.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myfood.R
import com.example.myfood.model.Usuario
import com.example.myfood.database.repositories.UsuarioRepository
import com.example.myfood.databinding.ActivityAutenticacaoBinding
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.utils.exibirMensagem
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


class AutenticacaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAutenticacaoBinding
    private val firebaseAuth: FirebaseAuth by lazy { ConfiguracaoFirebase().getFirebaseAutenticacao() }
    private val idUsuario: String by lazy { ConfiguracaoFirebase().getIdUsuario() }
    private val usuarioRepository = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutenticacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button = binding.buttonAcesso
        bindProgressButton(button)
        button.attachTextChangeAnimator()

        binding.switchAcesso.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.linearTipoUsuario.visibility = View.VISIBLE
            } else {
                binding.linearTipoUsuario.visibility = View.GONE
            }
        }

        binding.buttonAcesso.setOnClickListener {
            val email = binding.editCadastroEmail.text.toString()
            val senha = binding.editCadastroPassword.text.toString()

            button.showProgress {
                progressColor = Color.WHITE
                button.isEnabled = false
            }


            if (email.isEmpty()) {
                exibirMensagem("Preencha o E-mail!", this)
                binding.editCadastroEmail.error = "Preencha o E-mail!"
                hideLoadingButton(button)
                return@setOnClickListener
            }

            if (senha.isEmpty()) {
                exibirMensagem("Preencha a Senha!", this)
                binding.editCadastroPassword.error = "Preencha a Senha!"
                hideLoadingButton(button)
                return@setOnClickListener
            }

            if (binding.switchAcesso.isChecked) {
                firebaseAuth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            exibirMensagem("Cadastro realizado com sucesso!", this)

                            val tipoUsuario: String = getTipoUsuario()
                            val usuario = Usuario(email, tipoUsuario)

                            usuarioRepository.salvarUsuario(usuario)
                            hideLoadingButton(button)
                            abrirTelaPrincipal(usuario)
                        } else {
                            val erroExcecao: String = try {
                                throw task.exception!!
                            } catch (e: FirebaseAuthWeakPasswordException) {
                                "Digite uma senha mais forte!"
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                "E-mail inválido!"
                            } catch (e: FirebaseAuthUserCollisionException) {
                                "Este e-mail já está em uso!"
                            }

                            exibirMensagem("Error $erroExcecao", this)
                        }

                        hideLoadingButton(button)
                    }
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            usuarioRepository.buscarUsuario(idUsuario) { usuario ->
                                if (usuario != null) {
                                    exibirMensagem("Logado com sucesso!", this)
                                    abrirTelaPrincipal(usuario)
                                } else {
                                exibirMensagem("Erro ao buscar usuário", this)
                            }

                            hideLoadingButton(button)
                            }

                        } else {
                            exibirMensagem("Erro ao fazer login: " + task.exception, this)
                            hideLoadingButton(button)
                        }
                    }
            }
        }
    }

    private fun hideLoadingButton(button: Button) {
        button.hideProgress(R.string.progress_button_hide)
        button.isEnabled = true
    }

    private fun getTipoUsuario(): String {
        return if (binding.switchTipoUsuario.isChecked) "E" else "U"
    }

    private fun abrirTelaPrincipal(usuario: Usuario) {
        if (usuario.tipo == "E") {
            val intent = Intent(this, EmpresaActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        if (usuario.tipo == "U") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}