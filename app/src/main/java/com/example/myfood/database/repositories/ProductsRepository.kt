package com.example.myfood.database.repositories

import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ProductsRepository {
    private val firebaseReference: DatabaseReference by lazy { ConfiguracaoFirebase().getFirebase() }
    private val idUsuarioLogado: String by lazy { ConfiguracaoFirebase().getIdUsuario() }

    fun saveProduct(
        name: String,
        description: String,
        price: Double,
        image: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val product = Product(name, description, price, image)
        val ref = firebaseReference.child("usuarios").child(idUsuarioLogado).child("produtos").child(name)

        ref.setValue(product)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun fetchProducts(
        productList: MutableList<Product>,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val productsRef = firebaseReference.child("usuarios").child(idUsuarioLogado).child("produtos")

        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (products in snapshot.children) {
                    val product = products.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }
                onSuccess(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.toException())
            }
        })
    }

    fun deleteProduct(
        productName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val ref = firebaseReference.child("usuarios").child(idUsuarioLogado).child("produtos").child(productName)

        ref.removeValue()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}