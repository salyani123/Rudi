package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rudi.R
import com.example.rudi.databinding.FragmentGuidanceBinding

class GuidanceFragment : Fragment(R.layout.fragment_guidance) {
    private var _binding: FragmentGuidanceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGuidanceBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("E-Waste Guide")

        binding.btnNext.setOnClickListener {
            startFlow("pickup")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
