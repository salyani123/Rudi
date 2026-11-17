package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rudi.R
import com.example.rudi.databinding.FragmentRequestsBinding
import com.example.rudi.ui.viewmodel.PickupViewModel

class RequestsFragment : Fragment(R.layout.fragment_requests) {
    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: PickupViewModel by activityViewModels()
    private var openingScreen = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRequestsBinding.bind(view)
        openingScreen = false
        screenTitle(getString(R.string.requests_title))

        val adapter = RequestAdapter { request ->
            if (!openingScreen) {
                openingScreen = true
                (requireActivity() as MainActivity)
                    .openScreen(RequestDetailFragment.newInstance(request.id))
            }
        }
        binding.requestsList.layoutManager = LinearLayoutManager(requireContext())
        binding.requestsList.adapter = adapter
        viewModel.requests.observe(viewLifecycleOwner) { rows ->
            adapter.submitList(rows)
            binding.progressIndicator.isVisible = false
            binding.emptyText.isVisible = rows.isEmpty()
        }
        binding.newRequestButton.setOnClickListener {
            if (!openingScreen) {
                openingScreen = true
                startFlow("pickup")
            }
        }
    }

    override fun onDestroyView() {
        binding.requestsList.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
