package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.rudi.R
import com.example.rudi.databinding.FragmentGuidanceBinding
import androidx.fragment.app.activityViewModels
import com.example.rudi.data.DisposalGuides
import com.example.rudi.ui.viewmodel.CentreViewModel
import com.example.rudi.ui.viewmodel.DraftViewModel

class GuidanceFragment : Fragment(R.layout.fragment_guidance) {
    private var _binding: FragmentGuidanceBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val draft: DraftViewModel by activityViewModels()
    private val centres: CentreViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGuidanceBinding.bind(view)
        val item = draft.draft()
        screenTitle("Prepare your " + item.deviceType.lowercase())
        binding.itemTitle.text = itemTitle(item.deviceType, item.brand, item.model)
        val guidance = DisposalGuides.forItem(item.deviceType, item.condition)
        binding.guidanceText.text = buildString {
            guidance.steps.forEachIndexed { index, step ->
                if (index > 0) append("\n\n")
                appendLine("${index + 1}. ${step.title}")
                append(step.detail)
            }
            guidance.note?.let { append("\n\n").append(it) }
        }
        binding.centresButton.setOnClickListener {
            centres.setDevice(item.deviceType)
            (requireActivity() as MainActivity).showTopLevel(CentresFragment(), R.id.nav_centres)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
