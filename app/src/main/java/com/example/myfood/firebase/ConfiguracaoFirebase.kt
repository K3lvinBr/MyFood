package com.example.myfood.firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference




class ConfiguracaoFirebase {
    private val referenciaFirebase: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    private val referenciaAutenticacao: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val referenciaStorage: StorageReference by lazy { FirebaseStorage.getInstance().reference }

    fun getIdUsuario(): String {
        return referenciaAutenticacao.currentUser?.uid!!
    }

    fun getFirebase(): DatabaseReference {
        return referenciaFirebase
    }

    fun getFirebaseAutenticacao(): FirebaseAuth {
        return referenciaAutenticacao
    }

    fun getFirebaseStorage(): StorageReference {
        return referenciaStorage
    }
}