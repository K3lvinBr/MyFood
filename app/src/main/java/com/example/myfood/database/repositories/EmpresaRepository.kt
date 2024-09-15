package com.example.myfood.database.repositories

import android.util.Log
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.model.MoreShop
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class EmpresaRepository {
    private val database = ConfiguracaoFirebase()
    private val idUsuario: String by lazy { ConfiguracaoFirebase().getIdUsuario() }

    fun salvarEmpresa(
        empresa: MoreShop,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = database.getFirebase().child("usuarios").child(idUsuario).child("empresa")
        ref.setValue(empresa)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun recuperarEmpresa(): DatabaseReference {
       return database.getFirebase().child("usuarios").child(idUsuario).child("empresa")
    }

    suspend fun buscarEmpresas(): List<MoreShop> = suspendCancellableCoroutine { continuation ->
        val empresasRef = database.getFirebase().child("usuarios")

        empresasRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val empresas = mutableListOf<MoreShop>()

                for (usuarioSnapshot in snapshot.children) {
                    val tipo = usuarioSnapshot.child("tipo").getValue(String::class.java)
                    if (tipo == "E") {
                        val empresaSnapshot = usuarioSnapshot.child("empresa")
                        val empresa = empresaSnapshot.getValue(MoreShop::class.java)
                        if (empresa != null) {
                            empresas.add(empresa)
                        }
                    }
                }

                continuation.resume(empresas)
                Log.i("BuscarEmpresas", "empresas: $empresas")
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
                Log.e("BuscarEmpresas", "erro: $error")
            }
        })
    }

}