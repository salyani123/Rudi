package com.example.rudi.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rudi.R
import com.example.rudi.databinding.FragmentCentresBinding
import com.example.rudi.ui.viewmodel.CentreViewModel
import com.example.rudi.ui.viewmodel.Choices

class CentresFragment : Fragment(R.layout.fragment_centres) {
    private var _binding: FragmentCentresBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val centreModel: CentreViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCentresBinding.bind(view)
        screenTitle(getString(R.string.centres_title))
        binding.citySpinner.bindChoices(Choices.cities, centreModel.city, centreModel::setCity)
        binding.deviceSpinner.bindChoices(Choices.deviceFilter, centreModel.device, centreModel::setDevice)
        binding.citySpinner.isSaveEnabled = false
        binding.deviceSpinner.isSaveEnabled = false

        val adapter = CentreAdapter { openCentreMap(requireContext(), it.mapQuery) }
        binding.centresList.layoutManager = LinearLayoutManager(requireContext())
        binding.centresList.adapter = adapter
        centreModel.centres.observe(viewLifecycleOwner) {
            adapter.submitList(it.rows)
            render()
        }
        centreModel.loading.observe(viewLifecycleOwner) { render() }
        centreModel.error.observe(viewLifecycleOwner) { render() }
        centreModel.notice.observe(viewLifecycleOwner) { render() }
        binding.retryButton.setOnClickListener { centreModel.prepare() }
        // Refresh on every opening. The ViewModel guards concurrent retries.
        centreModel.prepare()
    }

    private fun render() {
        val result = centreModel.centres.value?.takeIf {
            it.city == centreModel.city && it.device == centreModel.device
        }
        val loading = centreModel.loading.value == true
        val error = centreModel.error.value
        val status = error ?: centreModel.notice.value
        binding.progressIndicator.isVisible = loading
        binding.statusText.text = status.orEmpty()
        binding.statusText.isVisible = status != null
        binding.retryButton.isVisible = status != null
        binding.retryButton.isEnabled = !loading
        binding.emptyText.isVisible = !loading && error == null &&
            result != null && result.rows.isEmpty()
        binding.emptyText.text = if (result?.cityEmpty == true) {
            getString(R.string.centres_city_empty, result.city)
        } else getString(R.string.centres_device_empty)
    }

    override fun onDestroyView() {
        binding.centresList.adapter = null
        binding.citySpinner.onItemSelectedListener = null
        binding.deviceSpinner.onItemSelectedListener = null
        _binding = null
        super.onDestroyView()
    }
}
