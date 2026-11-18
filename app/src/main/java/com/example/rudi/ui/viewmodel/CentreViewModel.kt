package com.example.rudi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.rudi.data.local.RudiDatabase
import com.example.rudi.data.model.CollectionCentre
import com.example.rudi.data.remote.ApiClient
import com.example.rudi.data.repository.CentreRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CentreViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = CentreRepository(RudiDatabase.get(app).centreDao(), ApiClient.centres)
    private val selection = MutableLiveData(Choices.cities.first() to Choices.ALL_DEVICES)
    val city get() = requireNotNull(selection.value).first
    val device get() = requireNotNull(selection.value).second

    data class CentreResult(val city: String, val device: String, val cityEmpty: Boolean, val rows: List<CollectionCentre>)

    val centres = selection.switchMap { (city, device) ->
        repository.observe(city, "").map { all ->
            val rows = all.filter {
                device == Choices.ALL_DEVICES || it.acceptedTypes.isBlank() || "|$device|" in it.acceptedTypes
            }
            CentreResult(city, device, all.isEmpty(), rows)
        }
    }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    private val _notice = MutableLiveData<String?>(null)
    val notice: LiveData<String?> = _notice

    fun setCity(newCity: String) {
        if (newCity in Choices.cities && newCity != city) selection.value = newCity to device
    }

    fun setDevice(newDevice: String) {
        if (newDevice in Choices.deviceFilter && newDevice != device) selection.value = city to newDevice
    }

    fun prepare() {
        if (_loading.value == true) return
        _loading.value = true
        _error.value = null
        _notice.value = null
        viewModelScope.launch {
            try {
                try {
                    repository.seedIfEmpty()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _error.value = "Could not load the directory. Try again."
                    return@launch
                }
                try {
                    if (repository.refresh() == 0) _notice.value = "No directory updates received. Showing saved centres."
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _notice.value = "Could not refresh. Showing saved centres."
                }
            } finally {
                _loading.value = false
            }
        }
    }
}
