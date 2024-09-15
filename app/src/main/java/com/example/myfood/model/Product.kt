package com.example.myfood.model

data class Product(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
) {
    constructor() : this("", "", 0.0, "")
}
