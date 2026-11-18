package com.example.rudi.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.rudi.R
import com.example.rudi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var topTab = R.id.nav_home
    private var updatingTab = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, keyboard.bottom))
            insets
        }
        topTab = savedInstanceState?.getInt("topTab") ?: R.id.nav_home
        binding.bottomNav.isSaveEnabled = false
        binding.bottomNav.selectedItemId = topTab
        binding.bottomNav.setOnItemSelectedListener { item ->
            if (!updatingTab && topTab != item.itemId) showTab(item.itemId)
            true
        }
        supportFragmentManager.addOnBackStackChangedListener { updateChrome() }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStack()
                    topTab != R.id.nav_home -> showTab(R.id.nav_home)
                    else -> finish()
                }
            }
        })
        if (savedInstanceState == null) showTab(R.id.nav_home)
        updateChrome()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("topTab", topTab)
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun updateChrome() {
        val pushed = supportFragmentManager.backStackEntryCount > 0
        binding.bottomNav.isVisible = !pushed
        supportActionBar?.setDisplayHomeAsUpEnabled(pushed)
    }

    fun setScreenTitle(title: String) { supportActionBar?.title = title }

    fun showTab(tabId: Int) {
        val fragment = when (tabId) {
            R.id.nav_centres -> CentresFragment()
            R.id.nav_requests -> RequestsFragment()
            else -> HomeFragment()
        }
        showTopLevel(fragment, tabId)
    }

    fun showTopLevel(fragment: Fragment, tabId: Int) {
        val manager = supportFragmentManager
        if (manager.isStateSaved) return
        topTab = tabId
        updatingTab = true
        binding.bottomNav.selectedItemId = tabId
        updatingTab = false
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        manager.beginTransaction().setReorderingAllowed(true).replace(R.id.screenContainer, fragment).commit()
    }

    fun openScreen(fragment: Fragment) {
        if (supportFragmentManager.isStateSaved) return
        supportFragmentManager.beginTransaction().setReorderingAllowed(true)
            .replace(R.id.screenContainer, fragment).addToBackStack(null).commit()
    }
}

fun Fragment.screenTitle(title: String) {
    (activity as? MainActivity)?.setScreenTitle(title)
}
