package com.example.rudi.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.rudi.data.model.PickupRequest

class DraftViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val defaults = mapOf(
        "deviceType" to "Laptop", "brand" to "", "model" to "", "condition" to "Not working",
        "quantity" to "1", "notes" to "", "address" to "", "date" to "", "slot" to pickupSlots.first()
    )
    val address = state.getLiveData("address", "")
    val date = state.getLiveData("date", "")
    val route: String get() = state["route"] ?: "dispose"
    val editingId: Long get() = state["editingId"] ?: 0L
    val editingCreatedAt: Long? get() = state["editingCreatedAt"]
    val flowId: Long get() = state["flowId"] ?: 0L

    fun get(key: String) = state.get<String>(key) ?: defaults.getValue(key)
    fun put(key: String, value: String) { state[key] = value }

    fun clear(newRoute: String = "dispose") {
        state["flowId"] = flowId + 1
        defaults.forEach { (key, value) -> put(key, value) }
        state["route"] = newRoute
        state["editingId"] = 0L
        state["editingCreatedAt"] = null
    }

    fun fillFrom(request: PickupRequest) {
        put("deviceType", request.deviceType)
        put("brand", request.brand)
        put("model", request.model)
        put("condition", request.condition)
        put("quantity", request.quantity.toString())
        put("notes", request.notes)
        put("address", request.address)
        put("date", request.pickupDate)
        put("slot", request.timeSlot)
        state["route"] = "pickup"
        state["editingId"] = request.id
        state["editingCreatedAt"] = request.createdAt
    }

    fun draft() = PickupDraft(
        get("deviceType"), get("brand"), get("model"), get("condition"), get("quantity"),
        get("notes"), get("address"), get("date"), get("slot")
    )
}
