package com.example.rudi.ui.viewmodel

import androidx.lifecycle.ViewModel

class DraftViewModel : ViewModel() {
    var deviceType: String = ""
    var brand: String = ""
    var model: String = ""
    var condition: String = ""
    var quantity: Int = 1
    var notes: String = ""
    var address: String = ""
    var pickupDate: String = ""
    var timeSlot: String = ""

    fun clear() {
        deviceType = ""
        brand = ""
        model = ""
        condition = ""
        quantity = 1
        notes = ""
        address = ""
        pickupDate = ""
        timeSlot = ""
    }
}
