package com.example.rudi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rudi.data.local.RudiDatabase
import com.example.rudi.data.remote.ApiClient
import com.example.rudi.data.model.CollectionCentre
import com.example.rudi.data.repository.CentreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CentreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CentreRepository(
        RudiDatabase.get(application).centreDao(),
        ApiClient.centres
    )

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun observeFiltered(city: String, type: String): LiveData<List<CollectionCentre>> {
        return repository.observe(city, type)
    }

    fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.seedIfEmpty()
            } catch (e: Exception) {
                // Ignore seed errors
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                repository.refresh()
                _error.postValue(null)
            } catch (e: Exception) {
                _error.postValue(e.localizedMessage ?: "Failed to refresh centres")
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
