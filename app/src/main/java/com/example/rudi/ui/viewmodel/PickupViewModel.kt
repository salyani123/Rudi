package com.example.rudi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.rudi.data.local.RudiDatabase
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.data.repository.PickupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PickupViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PickupRepository(RudiDatabase.get(application).pickupDao())
    val requests: LiveData<List<PickupRequest>> = repository.requests

    suspend fun get(id: Long): PickupRequest? = repository.get(id)
    suspend fun latestAddress(): String? = repository.latestAddress()

    fun save(request: PickupRequest, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.save(request)
            viewModelScope.launch(Dispatchers.Main) {
                onSaved(id)
            }
        }
    }

    fun delete(request: PickupRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(request)
        }
    }
}
