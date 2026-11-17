package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.rudi.R
import com.example.rudi.databinding.FragmentItemBinding
import com.example.rudi.ui.viewmodel.Choices
import com.example.rudi.ui.viewmodel.DraftViewModel

class ItemFragment : Fragment(R.layout.fragment_item) {
    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!
    private val draftViewModel: DraftViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("Device Details")

        var selectedDeviceType = Choices.deviceTypes.first()
        binding.spinnerDeviceType.bindChoices(Choices.deviceTypes, draftViewModel.deviceType.ifEmpty { null }) {
            selectedDeviceType = it
        }

        var selectedCondition = Choices.conditions.first()
        binding.spinnerCondition.bindChoices(Choices.conditions, draftViewModel.condition.ifEmpty { null }) {
            selectedCondition = it
        }

        binding.etBrand.setText(draftViewModel.brand)
        binding.etModel.setText(draftViewModel.model)
        binding.etQuantity.setText(if (draftViewModel.quantity > 0) draftViewModel.quantity.toString() else "1")
        binding.etNotes.setText(draftViewModel.notes)

        binding.btnNext.setOnClickListener {
            val brand = binding.etBrand.text.toString().trim()
            val model = binding.etModel.text.toString().trim()
            val qtyStr = binding.etQuantity.text.toString().trim()
            val quantity = qtyStr.toIntOrNull() ?: 1

            if (brand.isEmpty()) {
                binding.etBrand.error = "Brand is required"
                return@setOnClickListener
            }

            draftViewModel.deviceType = selectedDeviceType
            draftViewModel.brand = brand
            draftViewModel.model = model
            draftViewModel.condition = selectedCondition
            draftViewModel.quantity = quantity
            draftViewModel.notes = binding.etNotes.text.toString().trim()

            startFlow("guidance")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
