package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rudi.R
import com.example.rudi.databinding.FragmentPickupBinding
import com.example.rudi.ui.viewmodel.Choices
import com.example.rudi.ui.viewmodel.DraftViewModel

class PickupFragment : Fragment(R.layout.fragment_pickup) {
    private var _binding: FragmentPickupBinding? = null
    private val binding get() = _binding!!
    private val draftViewModel: DraftViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPickupBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("Pickup Schedule")

        binding.etAddress.setText(draftViewModel.address)
        binding.etDate.setText(draftViewModel.pickupDate)
        binding.etDate.bindDatePicker(parentFragmentManager)

        var selectedSlot = Choices.timeSlots.first()
        binding.spinnerTimeSlot.bindChoices(Choices.timeSlots, draftViewModel.timeSlot.ifEmpty { null }) {
            selectedSlot = it
        }

        binding.btnNext.setOnClickListener {
            val address = binding.etAddress.text.toString().trim()
            val date = binding.etDate.text.toString().trim()

            if (address.isEmpty()) {
                binding.etAddress.error = "Address is required"
                return@setOnClickListener
            }
            if (date.isEmpty()) {
                binding.etDate.error = "Date is required"
                return@setOnClickListener
            }

            draftViewModel.address = address
            draftViewModel.pickupDate = date
            draftViewModel.timeSlot = selectedSlot

            startFlow("review")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
