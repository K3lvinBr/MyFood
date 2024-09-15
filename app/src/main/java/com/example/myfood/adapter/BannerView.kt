package com.example.myfood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.databinding.BannerItemBinding
import com.example.myfood.model.Banner
import com.squareup.picasso.Picasso

class BannerView(private val items: List<Banner>) : RecyclerView.Adapter<BannerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BannerItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: BannerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Banner) {

            Picasso.get()
                .load(item.bannerUrl)
                .into(binding.imgBanner)
        }
    }
}