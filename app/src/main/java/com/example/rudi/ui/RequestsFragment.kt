package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rudi.R
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.databinding.FragmentRequestsBinding
import com.example.rudi.ui.viewmodel.PickupViewModel

class RequestsFragment : Fragment(R.layout.fragment_requests) {
    private var _binding: FragmentRequestsBinding? = null
    private val binding get() = _binding!!
    private val pickupViewModel: PickupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRequestsBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("Pickup Requests")

        val adapter = RequestAdapter()
        binding.recyclerRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRequests.adapter = adapter

        pickupViewModel.requests.observe(viewLifecycleOwner) { reqs ->
            adapter.submitList(reqs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class RequestAdapter : RecyclerView.Adapter<RequestAdapter.ViewHolder>() {
        private var items = listOf<PickupRequest>()

        fun submitList(list: List<PickupRequest>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(32, 32, 32, 32)
                textSize = 16f
            }
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val req = items[position]
            (holder.itemView as TextView).text = "${req.title()}\n${req.slotText()}\n${req.address}"
        }

        override fun getItemCount() = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
