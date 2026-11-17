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
import com.example.rudi.data.model.CollectionCentre
import com.example.rudi.databinding.FragmentCentresBinding
import com.example.rudi.ui.viewmodel.CentreViewModel

class CentresFragment : Fragment(R.layout.fragment_centres) {
    private var _binding: FragmentCentresBinding? = null
    private val binding get() = _binding!!
    private val centreViewModel: CentreViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCentresBinding.bind(view)
        (activity as? MainActivity)?.setScreenTitle("Collection Centres")

        centreViewModel.initData()

        val adapter = CentreAdapter()
        binding.recyclerCentres.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCentres.adapter = adapter

        centreViewModel.observeFiltered("Nairobi", "").observe(viewLifecycleOwner) { centres ->
            adapter.submitList(centres)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class CentreAdapter : RecyclerView.Adapter<CentreAdapter.ViewHolder>() {
        private var items = listOf<CollectionCentre>()

        fun submitList(list: List<CollectionCentre>) {
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
            val centre = items[position]
            (holder.itemView as TextView).text = "${centre.name} (${centre.city})\n${centre.address}"
        }

        override fun getItemCount() = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
