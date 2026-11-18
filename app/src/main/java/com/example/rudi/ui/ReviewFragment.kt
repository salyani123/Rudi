package com.example.rudi.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.rudi.R
import com.example.rudi.databinding.FragmentReviewBinding
import com.example.rudi.ui.viewmodel.DraftViewModel
import com.example.rudi.ui.viewmodel.PickupViewModel

class ReviewFragment : Fragment(R.layout.fragment_review) {
    private var _binding: FragmentReviewBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val draft: DraftViewModel by activityViewModels()
    private val pickup: PickupViewModel by activityViewModels()
    private var leaving = false
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted && isAdded) Toast.makeText(requireContext(), R.string.reminder_permission_denied, Toast.LENGTH_LONG).show()
        pickup.save(draft)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReviewBinding.bind(view)
        leaving = false
        screenTitle(getString(R.string.review_request))
        val item = draft.draft()
        binding.summaryText.text = getString(
            R.string.review_summary, itemTitle(item.deviceType, item.brand, item.model),
            item.deviceType, item.condition, item.quantity, item.address,
            displayDate(item.date) + " · " + item.slot.replace('-', '–'),
            item.notes.ifBlank { getString(R.string.request_no_notes) }
        )
        binding.saveButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                binding.saveButton.isEnabled = false
                permission.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                pickup.save(draft)
            }
        }
        pickup.busy.observe(viewLifecycleOwner) { busy ->
            binding.progressIndicator.isVisible = busy
            binding.saveButton.isEnabled = !busy && pickup.savedId.value == null
        }
        pickup.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                pickup.consumeMessage()
                if (pickup.busy.value != true && pickup.savedId.value == null) binding.saveButton.isEnabled = true
            }
        }
        pickup.savedId.observe(viewLifecycleOwner) { finishWhenResumed() }
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) finishWhenResumed()
        })
    }

    private fun finishWhenResumed() {
        if (leaving || pickup.savedId.value == null || parentFragmentManager.isStateSaved ||
            !viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
        leaving = true
        binding.saveButton.isEnabled = false
        draft.clear()
        (requireActivity() as MainActivity).showTopLevel(RequestsFragment(), R.id.nav_requests)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
