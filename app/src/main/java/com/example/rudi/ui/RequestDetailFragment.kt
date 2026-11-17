package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.rudi.R
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.databinding.FragmentRequestDetailBinding
import com.example.rudi.ui.viewmodel.DraftViewModel
import com.example.rudi.ui.viewmodel.PickupViewModel

class RequestDetailFragment : Fragment(R.layout.fragment_request_detail) {
    private var _binding: FragmentRequestDetailBinding? = null
    private val binding get() = requireNotNull(_binding)
    // Isolate selection/deletion from previously opened detail screens.
    private val viewModel: PickupViewModel by viewModels()
    private val flowModel: PickupViewModel by activityViewModels()
    private val draftModel: DraftViewModel by activityViewModels()
    private val requestId get() = requireArguments().getLong(ARG_ID)
    private var current: PickupRequest? = null
    private var leaving = false
    private var pendingExit = false
    private var pendingDelete = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRequestDetailBinding.bind(view)
        current = null
        leaving = false
        pendingExit = false
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) popWhenResumed()
        })
        screenTitle(getString(R.string.request_detail_title))
        setActionsEnabled(false)

        childFragmentManager.setFragmentResultListener(
            DeleteRequestDialogFragment.RESULT_KEY, viewLifecycleOwner
        ) { _, result ->
            if (!leaving && result.getLong(DeleteRequestDialogFragment.ARG_ID) == requestId) {
                pendingDelete = true
                deleteWhenReady()
            }
        }
        viewModel.select(requestId)
        viewModel.selected.observe(viewLifecycleOwner) { request ->
            if (leaving) return@observe
            if (request == null) {
                Toast.makeText(requireContext(), R.string.request_missing, Toast.LENGTH_LONG).show()
                leaveDetail()
            } else if (request.id == requestId) {
                current = request
                binding.progressIndicator.isVisible = false
                binding.summaryText.text = getString(
                    R.string.request_summary, request.title(), request.deviceType,
                    request.condition, request.quantity, request.address, request.slotText(),
                    request.notes.ifBlank { getString(R.string.request_no_notes) }
                )
                updateActions()
                deleteWhenReady()
            }
        }
        binding.calendarButton.setOnClickListener {
            current?.let { addPickupToCalendar(requireContext(), it) }
        }
        binding.editButton.setOnClickListener {
            val request = current ?: return@setOnClickListener
            if (leaving || viewModel.busy.value == true) return@setOnClickListener
            draftModel.clear("pickup")
            draftModel.fillFrom(request)
            flowModel.consumeSaved()
            leaving = true
            setActionsEnabled(false)
            (requireActivity() as MainActivity).openScreen(ItemFragment())
        }
        binding.deleteButton.setOnClickListener {
            if (childFragmentManager.isStateSaved ||
                childFragmentManager.findFragmentByTag(DeleteRequestDialogFragment.TAG) != null) {
                return@setOnClickListener
            }
            DeleteRequestDialogFragment.newInstance(requestId)
                .showNow(childFragmentManager, DeleteRequestDialogFragment.TAG)
        }
        viewModel.deletedId.observe(viewLifecycleOwner) { id ->
            if (id == requestId && !leaving) {
                Toast.makeText(requireContext(), R.string.request_deleted, Toast.LENGTH_SHORT).show()
                leaveDetail()
            }
        }
        viewModel.busy.observe(viewLifecycleOwner) {
            binding.progressIndicator.isVisible = it || (current == null && !leaving)
            updateActions()
        }
        viewModel.message.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                viewModel.consumeMessage()
            }
        }
    }

    private fun updateActions() {
        setActionsEnabled(!leaving && current != null && viewModel.busy.value != true &&
            viewModel.deletedId.value != requestId)
    }

    private fun deleteWhenReady() {
        val request = current ?: return
        if (pendingDelete && !leaving && viewModel.busy.value != true &&
            viewModel.deletedId.value != requestId) {
            pendingDelete = false
            viewModel.delete(request)
        }
    }

    private fun setActionsEnabled(enabled: Boolean) {
        binding.editButton.isEnabled = enabled
        binding.deleteButton.isEnabled = enabled
        binding.calendarButton.isEnabled = enabled
    }

    private fun leaveDetail() {
        leaving = true
        current = null
        setActionsEnabled(false)
        pendingExit = true
        popWhenResumed()
    }

    private fun popWhenResumed() {
        if (pendingExit && viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) &&
            !parentFragmentManager.isStateSaved) {
            pendingExit = false
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        current = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "requestId"

        fun newInstance(id: Long) = RequestDetailFragment().apply {
            require(id > 0) { "A saved request ID is required" }
            arguments = bundleOf(ARG_ID to id)
        }
    }
}
