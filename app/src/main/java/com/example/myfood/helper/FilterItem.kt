package com.example.myfood.helper

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import com.example.myfood.R
import com.google.android.material.chip.Chip

data class FilterItem(
    val id: Int,
    val text: String,
    @DrawableRes val icon: Int? = null,
    val iconSize: Float = 32.0f,
    @DrawableRes val closeIcon: Int? = null   //   @DrawableRes: para representar um icone desenhado
)

fun FilterItem.toChip(context: Context) : Chip {
    val chip = if (closeIcon == null) {
        LayoutInflater.from(context).inflate(R.layout.chip_choice, null, false) as Chip                 //   cria os Chip como estao no xml, podendo selecionar
    } else {
        Chip(ContextThemeWrapper(context, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice))   //   cria os Chip normais
    }

    if (closeIcon != null)
        chip.setChipBackgroundColorResource(R.color.white)   //   muda cor do Chip

    chip.setChipStrokeColorResource(R.color.lt_gray)         //   muda cor da borda

    chip.chipStrokeWidth = 2f                                //   muda grossura da borda

    if (icon != null) {
        chip.chipIconSize = iconSize                         //   tamanho do icone
        chip.setChipIconResource(icon)                       //   adiciona o icone na esquerda
        chip.chipStartPadding = 20f                          //   padding na esquerda
    } else {
        chip.chipIcon = null                                 //   desabilita o icone
    }

    closeIcon?.let {
        chip.setCloseIconResource(it)                        //   adiciona o icone na direita
        chip.isCloseIconVisible = true                       //   permite deixar o icone na direita visivel
    }

    chip.text = text

    return chip
}