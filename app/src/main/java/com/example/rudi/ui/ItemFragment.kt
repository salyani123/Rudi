package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rudi.R
import com.example.rudi.databinding.FragmentItemBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.rudi.ui.viewmodel.Choices
import com.example.rudi.ui.viewmodel.DraftViewModel

class ItemFragment : Fragment(R.layout.fragment_item) {
    private var _binding: FragmentItemBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val draft: DraftViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemBinding.bind(view)
        screenTitle(if (draft.editingId == 0L) "Item details" else "Edit item")
        binding.typeSpinner.bindChoices(Choices.deviceTypes, draft.get("deviceType")) { draft.put("deviceType", it) }
        binding.conditionSpinner.bindChoices(Choices.conditions, draft.get("condition")) { draft.put("condition", it) }
        mapOf(
            binding.brandInput to "brand", binding.modelInput to "model",
            binding.quantityInput to "quantity", binding.notesInput to "notes"
        ).forEach { (input, key) ->
            input.setText(draft.get(key))
            input.doAfterTextChanged { draft.put(key, it.toString()) }
        }
        binding.continueButton.setOnClickListener {
            val errors = draft.draft().itemErrors()
            binding.quantityInput.error = errors["quantity"]
            if (errors.isNotEmpty()) return@setOnClickListener
            binding.continueButton.isEnabled = false
            val next = if (draft.route == "dispose") GuidanceFragment() else PickupFragment()
            (requireActivity() as MainActivity).openScreen(next)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
