package com.example.myfood.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myfood.R
import com.example.myfood.activity.AutenticacaoActivity
import com.example.myfood.activity.ConfiguracoesUsuarioActivity
import com.example.myfood.databinding.FragmentNavigationHomeBinding
import com.example.myfood.firebase.ConfiguracaoFirebase
import com.example.myfood.viewModel.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class HomeNavigationFragment : Fragment(R.layout.fragment_navigation_home) {

    private lateinit var binding: FragmentNavigationHomeBinding
    private val autenticacao: FirebaseAuth by lazy { ConfiguracaoFirebase().getFirebaseAutenticacao() }
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNavigationHomeBinding.bind(view)

        binding.toolbarUser.title = ""
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarUser)
        setHasOptionsMenu(true) // Indica que o fragmento possui um menu

        handleCardAccount()

        setupViews()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_usuario, menu)

        val searchItem = menu.findItem(R.id.menuSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnSearchClickListener {
            binding.addressButton.isVisible = false
        }

        // Detectar o fechamento do SearchView
        searchView.setOnCloseListener {
            binding.addressButton.isVisible = true
            false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Aqui você pode aplicar o filtro quando o texto é submetido (opcional)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Aqui você aplica o filtro enquanto o texto é alterado

                sharedViewModel.searchQuery.value = newText
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSair -> {
                deslogarUsuario()
                return true
            }

            R.id.menuConfiguracoes -> {
                abrirConfiguracao()
                return true
            }

            R.id.menuSearch -> {

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun handleCardAccount() {
        autenticacao.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                binding.cardAccount.visibility = View.VISIBLE

                binding.cardAccount.setOnClickListener {
                    val intent = Intent(requireContext(), AutenticacaoActivity::class.java)
                    startActivity(intent)
                }
            } else {
                binding.cardAccount.visibility = View.GONE
            }
        }
    }

    private fun setupViews() {
        val tabLayout = binding.addTab
        val viewPager = binding.addViewpager
        val adapter = TabViewPagerAdapter(requireActivity())
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false   //   inpedir de scrolar arrastando o dedo

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(adapter.tabs[position])
        }.attach()   //   attach: para finalizar e sincronizar o TabLayout com os itens do ViewPager.
    }

    private fun deslogarUsuario() {
        try {
            autenticacao.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun abrirConfiguracao() {
        startActivity(Intent(requireContext(), ConfiguracoesUsuarioActivity::class.java))
    }
}

class TabViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    val tabs = arrayOf(R.string.restaurants, R.string.marketplaces, R.string.drinks)
    val fragments = arrayOf(RestaurantFragment(), MarketplaceFragment(), MarketplaceFragment())

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}

class MarketplaceFragment : Fragment() {}