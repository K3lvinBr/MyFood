package com.example.myfood.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myfood.R
import com.example.myfood.adapter.BannerView
import com.example.myfood.adapter.CategoryView
import com.example.myfood.adapter.MoreShopView
import com.example.myfood.adapter.ShopView
import com.example.myfood.database.repositories.EmpresaRepository
import com.example.myfood.databinding.FragmentRestaurantBinding
import com.example.myfood.helper.FilterItem
import com.example.myfood.helper.toChip
import com.example.myfood.model.Banner
import com.example.myfood.model.Category
import com.example.myfood.model.MoreShop
import com.example.myfood.model.Shop
import com.example.myfood.viewModel.SharedViewModel
import kotlinx.coroutines.launch

class RestaurantFragment : Fragment(R.layout.fragment_restaurant) {

    private var binding: FragmentRestaurantBinding? = null
    private lateinit var empresaRepository: EmpresaRepository
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var filters = arrayOf(
        FilterItem(1, "Ordenar", closeIcon = R.drawable.baseline_keyboard_arrow_down_24),
        FilterItem(2, "Pra retirar", icon = R.drawable.baseline_directions_walk_24),
        FilterItem(3, "Entrega grátis"),
        FilterItem(4, "Vale-refeição", closeIcon = R.drawable.baseline_keyboard_arrow_down_24),
        FilterItem(5, "Distância", closeIcon = R.drawable.baseline_keyboard_arrow_down_24),
        FilterItem(6, "Entrega Parceira"),
        FilterItem(7, "Super Restaurante"),
        FilterItem(8, "Filtros", closeIcon = R.drawable.baseline_filter_list_24),
    )

    private val dataListCategory = arrayListOf(
        Category(1, "https://www.ifood.com.br/static/images/categories/market.png", "Mercado", 0xFFB6D048),
        Category(2, "https://www.ifood.com.br/static/images/categories/restaurant.png", "Restaurante", 0xFFE91D2D),
        Category(3, "https://www.ifood.com.br/static/images/categories/drinks.png", "Bebidas", 0xFFF6D553),
        Category(4, "https://www.ifood.com.br/static/images/categories/express.png", "Express", 0xFFFF0000),
        Category(5, "https://static.ifood-static.com.br/image/upload/f_auto/webapp/landingV2/petshop.png", "Saudável", 0xFFE91D2D),
        Category(6, "https://www.ifood.com.br/static/images/categories/drinks.png", "Salgados", 0xFF8C60C5),
    )

    private val dataListBanner = arrayListOf(
        Banner(1, "https://static.ifood-static.com.br/image/upload/t_high,q_100/webapp/landing/landing-banner-1.png"),
        Banner(2, "https://static.ifood-static.com.br/image/upload/t_high,q_100/webapp/landing/landing-banner-2.png"),
        Banner(3, "https://static.ifood-static.com.br/image/upload/t_high,q_100/webapp/landing/landing-banner-3.png"),
    )

    private val dataListShop = mutableListOf<Shop>()

    private val dataListMoreShop = mutableListOf<MoreShop>()
    private var filteredListMoreShop = mutableListOf<MoreShop>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {   //   dispara quando a View esta pronta
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRestaurantBinding.bind(view)
        empresaRepository = EmpresaRepository()

        lifecycleScope.launch {
            try {
                val empresas = empresaRepository.buscarEmpresas()
                dataListMoreShop.addAll(empresas)
                dataListShop.addAll(empresas.map { empresa ->
                    Shop(
                        id = empresa.id,
                        shopUrl = empresa.bannerUrl,
                        text = empresa.text
                    )
                })
                filteredListMoreShop = dataListMoreShop.toMutableList()

                binding?.rvMoreShop?.adapter = MoreShopView(filteredListMoreShop, ::handleClickShops)

                binding?.rvMoreShop?.adapter?.notifyDataSetChanged()
                binding?.rvShops?.adapter?.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("FirebaseError", "Erro ao buscar empresas: ${e.message}")
            }
        }

        binding?.let {
            it.rvCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)   //   criar o layout do ReclyclerView na HORIZONTAL
            it.rvCategory.adapter = CategoryView(dataListCategory)

            it.rvBanners.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            it.rvBanners.adapter = BannerView(dataListBanner)

            it.rvShops.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            it.rvShops.adapter = ShopView(dataListShop)

            it.rvMoreShop.layoutManager = LinearLayoutManager(requireContext())
            it.rvMoreShop.adapter = MoreShopView(filteredListMoreShop, ::handleClickShops)

            snapHelper.attachToRecyclerView(binding?.rvBanners)

            it.rvBanners.addOnScrollListener(object : RecyclerView.OnScrollListener() {          //   função que executa quando mexer no RecyclerView
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {                            //   SCROLL_STATE_IDLE: estado em que não há nenhuma rolagem ocorrendo
                        notifyPositionChanged(recyclerView)
                    }
                }
            })

            addDots(it.dots, dataListBanner.size, 0)

            filters.forEach { filter ->
                it.chipGroupFilter.addView(filter.toChip(requireContext()))   //   requireContext(): chamar contexto em Fragment
            }
        }

        sharedViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            Log.i("pesquisa:", query.toString())
            filterListMoreShops(query)
        }
    }

    private fun handleClickShops(shop: MoreShop) {

    }

    private fun addDots(container: LinearLayout, size: Int, position: Int) {
        container.removeAllViews()

        Array(size) {   //   como se fosse um for(), it valor atual do loop
            val textView = TextView(context).apply {
                text = getString(R.string.dotted)
                textSize = 20f
                setTextColor(
                    if (position == it) ContextCompat.getColor(context, android.R.color.black)
                    else ContextCompat.getColor(context, android.R.color.darker_gray)
                )

            }
            container.addView(textView)
        }
    }

    private var position: Int? = RecyclerView.NO_POSITION   //   NO_POSITION: indica a ausência de uma posição válida
    private val snapHelper = LinearSnapHelper()

    private fun notifyPositionChanged(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        val view = snapHelper.findSnapView(layoutManager)                                                 //   encontrar a visualização atualmente encaixada no RecyclerView
        val position = if (view == null) RecyclerView.NO_POSITION else layoutManager?.getPosition(view)   //   obter a posição do item correspondente à visualização encaixada.

        val positionChanged = this.position != position
        if (positionChanged) {
            addDots(binding!!.dots, dataListBanner.size, position ?: 0)
        }
        this.position = position
    }

    private fun filterListMoreShops(query: String?) {
        Log.i("valorPesquisa:", query.toString())
        Log.i("valorPesquisa:", dataListMoreShop.toString())
        Log.i("valorPesquisa:", filteredListMoreShop.toString())
        if (query.isNullOrEmpty()) {
            filteredListMoreShop = dataListMoreShop.toMutableList()
        } else {
            filteredListMoreShop = dataListMoreShop.filter {
                it.text.contains(query, ignoreCase = true)
            }.toMutableList()
        }

        (binding?.rvMoreShop?.adapter as? MoreShopView)?.updateList(filteredListMoreShop)
    }
}