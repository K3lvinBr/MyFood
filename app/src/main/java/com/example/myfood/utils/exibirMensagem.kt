package com.example.myfood.utils

import android.content.Context
import android.widget.Toast

fun exibirMensagem(text: String, context: Context) {
    return Toast.makeText(
        context,
        text,
        Toast.LENGTH_SHORT
    ).show()
}