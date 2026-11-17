package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.rudi.R
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.databinding.FragmentReviewBinding
import com.example.rudi.ui.viewmodel.DraftViewModel
import com.example.rudi.ui.viewmodel.PickupViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReviewFragment : Fragment(R.layout.fragment_review) {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = _binding!!
    private val draftViewModel: DraftViewModel by activityViewModels()
    private val pickupViewModel: PickupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReviewBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("Review Request")

        val summary = buildString {
            append("Device: ").append(draftViewModel.deviceType).append("\n")
            append("Brand/Model: ").append(draftViewModel.brand).append(" ").append(draftViewModel.model).append("\n")
            append("Condition: ").append(draftViewModel.condition).append("\n")
            append("Quantity: ").append(draftViewModel.quantity).append("\n")
            if (draftViewModel.notes.isNotBlank()) {
                append("Notes: ").append(draftViewModel.notes).append("\n")
            }
            append("Address: ").append(draftViewModel.address).append("\n")
            append("Date: ").append(draftViewModel.pickupDate).append("\n")
            append("Time Slot: ").append(draftViewModel.timeSlot)
        }
        binding.tvSummary.text = summary

        binding.btnSubmit.setOnClickListener {
            val request = PickupRequest(
                deviceType = draftViewModel.deviceType,
                brand = draftViewModel.brand,
                model = draftViewModel.model,
                condition = draftViewModel.condition,
                quantity = draftViewModel.quantity,
                notes = draftViewModel.notes,
                address = draftViewModel.address,
                pickupDate = draftViewModel.pickupDate,
                timeSlot = draftViewModel.timeSlot
            )
            pickupViewModel.save(request) {
                draftViewModel.clear()
                (activity as? MainActivity)?.showTopLevel(RequestsFragment(), "REQUESTS")
                (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottomNav)?.selectedItemId = R.id.nav_requests
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
