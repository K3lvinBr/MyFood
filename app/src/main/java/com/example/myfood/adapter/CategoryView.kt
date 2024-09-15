package com.example.myfood.adapter


import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.databinding.CategoryItemBinding
import com.example.myfood.model.Category
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CategoryView(private val items: List<Category>) : RecyclerView.Adapter<CategoryView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category) {
            binding.txtCategory.text = item.name

            Picasso.get()
                .load(item.logoUrl)
                .into(binding.imgCategory, object : Callback {   //   object: cria uma classe anonima
                    override fun onSuccess() {                   //   será chamado quando a imagem carregar
                        val shape = GradientDrawable()           //   permite criar e personalizar formas gráficas
                        shape.cornerRadius = 10f

                        shape.setColor(item.color.toInt())

                        binding.bgCategory.background = shape
                    }

                    override fun onError(e: Exception?) {
                    }
                })
        }
    }
}