package com.example.rudi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rudi.R
import com.example.rudi.data.model.CollectionCentre
import com.example.rudi.databinding.RowCentreBinding

class CentreAdapter(private val onMap: (CollectionCentre) -> Unit) :
    ListAdapter<CollectionCentre, CentreAdapter.Holder>(Diff) {

    init {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    object Diff : DiffUtil.ItemCallback<CollectionCentre>() {
        override fun areItemsTheSame(old: CollectionCentre, new: CollectionCentre) = old.id == new.id
        override fun areContentsTheSame(old: CollectionCentre, new: CollectionCentre) = old == new
    }

    class Holder(val binding: RowCentreBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        RowCentreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val centre = getItem(position)
        with(holder.binding) {
            nameText.text = centre.name
            addressText.text = centre.address.ifBlank { root.context.getString(R.string.centre_address_pending) }
            hoursText.text = centre.hours.ifBlank { root.context.getString(R.string.centre_hours_pending) }
            requirementsText.text = centre.requirements.ifBlank {
                root.context.getString(R.string.centre_contact_requirements)
            }
            acceptedText.text = centre.acceptedTypes.split('|').filter { it.isNotBlank() }
                .joinToString(", ").ifBlank { root.context.getString(R.string.centre_confirm_acceptance) }
            verificationText.text = centre.verificationNote
            verificationText.isVisible = centre.verificationNote.isNotBlank()
            directionsButton.isEnabled = centre.mapQuery.isNotBlank()
            directionsButton.setOnClickListener { onMap(centre) }
        }
    }
}
