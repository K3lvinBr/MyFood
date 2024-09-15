package com.example.myfood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.model.Product
import com.example.myfood.databinding.ProductItemBinding
import com.squareup.picasso.Picasso

class EmpresaProductView(private val itens: List<Product>, private val onItemLongClick: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText: TextView = (view as LinearLayout).getChildAt(0) as TextView
    }

    class ItemViewHolder(private val binding: ProductItemBinding, onItemLongClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnLongClickListener {
                onItemLongClick(adapterPosition - 1)
                true
            }
        }

        fun bind(item: Product) {

            binding.productName.text = item.name
            binding.productDescription.text = item.description
            binding.productPrice.text = item.price.toString()

            Picasso.get()
                .load(R.drawable.product_background)
                .placeholder(R.drawable.product_background)
                .error(R.drawable.product_background)
                .into(binding.productImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.header_empresa_recycler_view, parent, false)
            HeaderViewHolder(view)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ProductItemBinding.inflate(inflater, parent, false)
            return ItemViewHolder(binding, onItemLongClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            // Handle header binding if needed
        } else if (holder is ItemViewHolder) {
            val itemPosition = position - 1
            holder.bind(itens[itemPosition])
        }
    }

    override fun getItemCount(): Int = itens.size + 1
}