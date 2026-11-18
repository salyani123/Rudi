package com.example.rudi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.rudi.data.local.RudiDatabase
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.data.repository.PickupRepository
import com.example.rudi.work.ReminderScheduler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PickupViewModel(private val app: Application) : AndroidViewModel(app) {
    private val repository = PickupRepository(RudiDatabase.get(app).pickupDao())
    val requests = repository.requests

    private val _selected = MutableLiveData<PickupRequest?>()
    val selected: LiveData<PickupRequest?> = _selected
    private val _readError = MutableLiveData(false)
    val readError: LiveData<Boolean> = _readError
    private var selection: Job? = null

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy
    private val _message = MutableLiveData<String?>(null)
    val message: LiveData<String?> = _message
    private val _savedId = MutableLiveData<Long?>(null)
    val savedId: LiveData<Long?> = _savedId
    private val _deletedId = MutableLiveData<Long?>(null)
    val deletedId: LiveData<Long?> = _deletedId

    fun select(id: Long) {
        selection?.cancel()
        _readError.value = false
        selection = viewModelScope.launch {
            try {
                _selected.value = repository.get(id)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _readError.value = true
            }
        }
    }

    fun prefillAddress(draft: DraftViewModel) {
        val flowId = draft.flowId
        viewModelScope.launch {
            try {
                val address = repository.latestAddress() ?: return@launch
                if (draft.flowId == flowId && draft.editingId == 0L && draft.get("address").isEmpty()) {
                    draft.put("address", address)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _message.value = "Could not load your last address. Enter it manually."
            }
        }
    }

    fun save(draft: DraftViewModel) {
        if (_busy.value == true || _savedId.value != null) return
        val now = java.time.LocalDateTime.now(nairobiZone)
        val details = draft.draft()
        val error = details.errors(now).values.firstOrNull()
        if (error != null) {
            _message.value = error
            return
        }
        val request = details.toRequest(draft.editingId, draft.editingCreatedAt, now)
        _busy.value = true
        viewModelScope.launch {
            try {
                val id = repository.save(request)
                try {
                    ReminderScheduler.schedule(app, request.copy(id = id))
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _message.value = "Saved. The reminder could not be scheduled."
                } finally {
                    _savedId.value = id
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _message.value = "Could not save the request. Try again."
            } finally {
                _busy.value = false
            }
        }
    }

    fun delete(request: PickupRequest) {
        if (_busy.value == true || _deletedId.value == request.id) return
        _busy.value = true
        viewModelScope.launch {
            try {
                repository.delete(request)
                try {
                    ReminderScheduler.cancel(app, request.id)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    _message.value = "Deleted. The reminder could not be cancelled."
                } finally {
                    _deletedId.value = request.id
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _message.value = "Could not delete the request. Try again."
            } finally {
                _busy.value = false
            }
        }
    }

    fun consumeSaved() { _savedId.value = null }
    fun consumeMessage() { _message.value = null }
}
