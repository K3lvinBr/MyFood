package com.example.myfood.model

data class MoreShop(
    val id: String,
    val bannerUrl: String,
    val text: String,
    val rate: Double,
    val category: String,
    val distance: Double,
    val time: String,
    val price: Double
) {
    constructor() : this("", "", "", 0.0, "", 0.0, "", 0.0)
}