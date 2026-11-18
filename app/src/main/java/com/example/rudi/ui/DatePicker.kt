package com.example.rudi.ui

import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun EditText.bindDatePicker(fragmentManager: FragmentManager) {
    isFocusable = false
    isClickable = true
    setOnClickListener {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Pickup Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        picker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            setText(sdf.format(Date(selection)))
        }
        picker.show(fragmentManager, "DATE_PICKER")
    }
}
