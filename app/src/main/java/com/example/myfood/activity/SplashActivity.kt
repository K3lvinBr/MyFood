package com.example.myfood.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myfood.R
import com.example.myfood.database.repositories.UsuarioRepository
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.model.Usuario
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private val firebaseAuth: FirebaseAuth by lazy { ConfiguracaoFirebase().getFirebaseAutenticacao() }
    private val usuarioRepository = UsuarioRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            verificarUsuarioLogado()
        }, 3000)

    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null) {
            usuarioRepository.buscarUsuario(usuarioAtual.uid) { usuario ->
                if (usuario != null) {
                    abrirTelaPrincipal(usuario)
                }
            }
        } else {
//            val intent = Intent(this, AutenticacaoActivity::class.java)
//            startActivity(intent)
//            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun abrirTelaPrincipal(usuario: Usuario) {
        if (usuario.tipo == "E") {
            val intent = Intent(this, EmpresaActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (usuario.tipo == "U") {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}