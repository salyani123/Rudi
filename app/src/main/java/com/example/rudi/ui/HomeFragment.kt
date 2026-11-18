package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rudi.R
import com.example.rudi.databinding.FragmentHomeBinding
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rudi.ui.viewmodel.PickupViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val pickupModel: PickupViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        screenTitle(getString(R.string.app_name))
        val host = requireActivity() as MainActivity
        val adapter = RequestAdapter { host.openScreen(RequestDetailFragment.newInstance(it.id)) }
        binding.requestsList.layoutManager = LinearLayoutManager(requireContext())
        binding.requestsList.adapter = adapter
        pickupModel.requests.observe(viewLifecycleOwner) { rows ->
            adapter.submitList(rows.take(3))
            binding.emptyText.isVisible = rows.isEmpty()
        }
        binding.disposeButton.setOnClickListener { startFlow("dispose") }
        binding.pickupButton.setOnClickListener { startFlow("pickup") }
        binding.requestsButton.setOnClickListener { host.showTab(R.id.nav_requests) }
    }

    override fun onDestroyView() {
        binding.requestsList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
