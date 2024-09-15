package com.example.myfood.model

data class Usuario(
    var email: String = "",
    var tipo: String = "",
) {
    constructor() : this("", "")
}