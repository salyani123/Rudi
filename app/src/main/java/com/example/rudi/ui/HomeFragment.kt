package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rudi.R
import com.example.rudi.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("Rudi E-Waste")

        binding.btnStartPickup.setOnClickListener {
            startFlow("item")
        }
        binding.btnViewCentres.setOnClickListener {
            (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_centres
        }
        binding.btnViewRequests.setOnClickListener {
            (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_requests
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
