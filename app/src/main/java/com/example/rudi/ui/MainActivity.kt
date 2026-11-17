package com.example.rudi.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.rudi.R
import com.example.rudi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showTopLevel(HomeFragment(), "HOME")
                    true
                }
                R.id.nav_centres -> {
                    showTopLevel(CentresFragment(), "CENTRES")
                    true
                }
                R.id.nav_requests -> {
                    showTopLevel(RequestsFragment(), "REQUESTS")
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_home
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm = supportFragmentManager
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack()
                } else {
                    if (binding.bottomNav.selectedItemId != R.id.nav_home) {
                        binding.bottomNav.selectedItemId = R.id.nav_home
                    } else {
                        finish()
                    }
                }
            }
        })
    }

    fun setScreenTitle(title: String) {
        supportActionBar?.title = title
    }

    fun showTopLevel(fragment: Fragment, tag: String) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.screenContainer, fragment, tag)
            .commit()
    }

    fun replaceTopLevel(fragment: Fragment, tag: String) {
        showTopLevel(fragment, tag)
    }

    fun openScreen(route: String) {
        val fragment: Fragment = when (route) {
            "item" -> ItemFragment()
            "guidance" -> GuidanceFragment()
            "pickup" -> PickupFragment()
            "review" -> ReviewFragment()
            "home" -> HomeFragment()
            "centres" -> CentresFragment()
            "requests" -> RequestsFragment()
            else -> ItemFragment()
        }
        openScreen(fragment)
    }

    fun openScreen(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.screenContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
