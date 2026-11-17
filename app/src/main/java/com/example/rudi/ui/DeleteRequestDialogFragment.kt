package com.example.rudi.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.rudi.R

/** FragmentManager restores this confirmation if the device rotates while it is open. */
class DeleteRequestDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.request_delete_title)
            .setMessage(R.string.request_delete_message)
            .setNegativeButton(R.string.request_keep, null)
            .setPositiveButton(R.string.request_delete) { _, _ ->
                parentFragmentManager.setFragmentResult(
                    RESULT_KEY,
                    bundleOf(ARG_ID to requireArguments().getLong(ARG_ID))
                )
            }
            .create()

    companion object {
        const val TAG = "deletePickupRequest"
        const val RESULT_KEY = "confirmDeletePickupRequest"
        const val ARG_ID = "requestId"

        fun newInstance(id: Long) = DeleteRequestDialogFragment().apply {
            arguments = bundleOf(ARG_ID to id)
        }
    }
}
