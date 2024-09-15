package com.example.myfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.databinding.MoreShopItemBinding
import com.example.myfood.model.MoreShop
import com.squareup.picasso.Picasso

class MoreShopView(private val items: MutableList<MoreShop>,
                   private val onItemClick: (MoreShop) -> Unit
    ) : RecyclerView.Adapter<MoreShopView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MoreShopItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<MoreShop>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: MoreShopItemBinding,
        private val onItemClick: (MoreShop) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MoreShop) {

            if (item.bannerUrl.isEmpty()) {
                Picasso.get()
                    .load(R.drawable.product_background)
                    .into(binding.imgShop)
            } else {
                Picasso.get()
                    .load(item.bannerUrl)
                    .into(binding.imgShop)
            }

            binding.txtShop.text = item.text
            binding.txtStar.text = item.rate.toString()
            binding.txtSubtitle.text = itemView.context.getString(R.string.shop_category, item.category, item.distance)
            binding.txtPrice.text = itemView.context.getString(R.string.shop_price, item.time, item.price)
            // colocar string variavel, getString(R.string.nome, item1, item2)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}