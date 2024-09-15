package com.example.myfood.database.repositories

import android.util.Log
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.model.Usuario

class UsuarioRepository {
    val database = ConfiguracaoFirebase()

    fun salvarUsuario(usuario: Usuario) {
        val idUsuario = database.getIdUsuario()

        val ref = database.getFirebase().child("usuarios").child(idUsuario)
        ref.setValue(usuario)
            .addOnSuccessListener {
                Log.i("UsuarioRepository", "Usuário salvo com sucesso")
            }
            .addOnFailureListener { excecao ->
                Log.e("UsuarioRepository", "Erro ao salvar usuário", excecao)
            }
    }

    fun buscarUsuario(id: String, callback: (Usuario?) -> Unit) {
        val ref = database.getFirebase().child("usuarios").child(id)
        ref.get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val usuario = dataSnapshot.getValue(Usuario::class.java)
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { excecao ->
                callback(null)
            }
    }
}