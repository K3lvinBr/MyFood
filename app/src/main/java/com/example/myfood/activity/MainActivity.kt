package com.example.myfood.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myfood.fragment.HomeNavigationFragment
import com.example.myfood.R
import com.example.myfood.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_1 -> {
                    loadFragment(HomeNavigationFragment())
                    true
                }

                R.id.item_2 -> {
                    loadFragment(SearchFragment())
                    true
                }

                R.id.item_3 -> {
                    loadFragment(OrdersFragment())
                    true
                }

                R.id.item_4 -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        // Define o fragmento inicial
        loadFragment(HomeNavigationFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}

class SearchFragment : Fragment() {}
class OrdersFragment : Fragment() {}
class ProfileFragment : Fragment() {}