package com.example.myfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.databinding.ShopItemBinding
import com.example.myfood.model.Shop
import com.squareup.picasso.Picasso

class ShopView(private val items: List<Shop>) : RecyclerView.Adapter<ShopView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ShopItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: ShopItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Shop) {

            if (item.shopUrl.isEmpty()) {
                Picasso.get()
                    .load(R.drawable.product_background)
                    .into(binding.imgShop)
            } else {
                Picasso.get()
                    .load(item.shopUrl)
                    .into(binding.imgShop)
            }

            binding.txtShop.text = item.text
        }
    }
}