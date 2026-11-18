package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rudi.R
import com.example.rudi.databinding.FragmentPickupBinding
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.rudi.ui.viewmodel.DraftViewModel
import com.example.rudi.ui.viewmodel.pickupSlots

class PickupFragment : Fragment(R.layout.fragment_pickup) {
    private var _binding: FragmentPickupBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val draft: DraftViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPickupBinding.bind(view)
        screenTitle("Schedule pickup")
        binding.addressInput.setText(draft.get("address"))
        binding.addressInput.doAfterTextChanged { draft.put("address", it.toString()) }
        draft.address.observe(viewLifecycleOwner) { address ->
            if (binding.addressInput.text.toString() != address) binding.addressInput.setText(address)
        }
        binding.slotSpinner.bindChoices(pickupSlots, draft.get("slot")) { draft.put("slot", it) }
        draft.date.observe(viewLifecycleOwner) { iso ->
            binding.dateButton.text = if (iso.isBlank()) getString(R.string.choose_date) else displayDate(iso)
        }
        bindDatePicker(childFragmentManager, draft)
        binding.dateButton.setOnClickListener { bindDatePicker(childFragmentManager, draft, show = true) }
        binding.reviewButton.setOnClickListener {
            val errors = draft.draft().errors()
            binding.addressInput.error = errors["address"]
            binding.dateError.text = errors["date"].orEmpty()
            binding.dateError.isVisible = "date" in errors
            if (errors.isEmpty()) {
                binding.reviewButton.isEnabled = false
                (requireActivity() as MainActivity).openScreen(ReviewFragment())
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
