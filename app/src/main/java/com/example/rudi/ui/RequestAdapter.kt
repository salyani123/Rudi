package com.example.rudi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rudi.R
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.databinding.RowRequestBinding

class RequestAdapter(private val onClick: (PickupRequest) -> Unit) :
    ListAdapter<PickupRequest, RequestAdapter.Holder>(Diff) {

    init {
        stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    object Diff : DiffUtil.ItemCallback<PickupRequest>() {
        override fun areItemsTheSame(old: PickupRequest, new: PickupRequest) = old.id == new.id
        override fun areContentsTheSame(old: PickupRequest, new: PickupRequest) = old == new
    }

    class Holder(val binding: RowRequestBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        RowRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val request = getItem(position)
        with(holder.binding) {
            titleText.text = if (request.quantity > 1) {
                root.context.getString(R.string.request_title_quantity, request.title(), request.quantity)
            } else request.title()
            addressText.text = request.address
            slotText.text = request.slotText()
            root.setOnClickListener { onClick(request) }
        }
    }
}
