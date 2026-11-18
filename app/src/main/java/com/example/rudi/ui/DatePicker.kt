package com.example.rudi.ui

import androidx.fragment.app.FragmentManager
import com.example.rudi.ui.viewmodel.DraftViewModel
import com.example.rudi.ui.viewmodel.nairobiZone
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private const val PICKER_TAG = "pickupDatePicker"

fun bindDatePicker(manager: FragmentManager, draft: DraftViewModel, show: Boolean = false) {
    @Suppress("UNCHECKED_CAST")
    val restored = manager.findFragmentByTag(PICKER_TAG) as? MaterialDatePicker<Long>
    if (restored == null && (!show || manager.isStateSaved)) return
    val today = LocalDate.now(nairobiZone)
    fun utcMillis(date: LocalDate) = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val selection = runCatching { LocalDate.parse(draft.get("date")) }
        .getOrNull()?.takeUnless { it.isBefore(today) } ?: today
    val picker = restored ?: MaterialDatePicker.Builder.datePicker()
        .setTitleText("Preferred date")
        .setSelection(utcMillis(selection))
        .setCalendarConstraints(CalendarConstraints.Builder().setStart(utcMillis(today))
            .setValidator(DateValidatorPointForward.from(utcMillis(today))).build())
        .build()
    picker.clearOnPositiveButtonClickListeners()
    picker.addOnPositiveButtonClickListener { millis ->
        val selected = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
        if (!selected.isBefore(LocalDate.now(nairobiZone))) draft.put("date", selected.toString())
    }
    if (restored == null) picker.showNow(manager, PICKER_TAG)
}
