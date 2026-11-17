package com.example.rudi.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.example.rudi.R
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.ui.viewmodel.nairobiZone
import com.example.rudi.ui.viewmodel.pickupWindow

fun openCentreMap(context: Context, query: String) {
    if (query.isBlank()) return
    // An HTTPS Maps URL can also open in a browser when Maps is not installed.
    val uri = Uri.parse("https://www.google.com/maps/search/").buildUpon()
        .appendQueryParameter("api", "1")
        .appendQueryParameter("query", query.trim())
        .build()
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (error: ActivityNotFoundException) {
        Toast.makeText(context, R.string.maps_unavailable, Toast.LENGTH_LONG).show()
    }
}

fun addPickupToCalendar(context: Context, request: PickupRequest) {
    val window = pickupWindow(request.pickupDate, request.timeSlot)
    if (window == null) {
        Toast.makeText(context, R.string.calendar_invalid_slot, Toast.LENGTH_LONG).show()
        return
    }
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, context.getString(R.string.calendar_event_title))
        putExtra(CalendarContract.Events.EVENT_LOCATION, request.address)
        putExtra(CalendarContract.Events.DESCRIPTION, context.getString(R.string.pickup_local_notice))
        putExtra(CalendarContract.Events.EVENT_TIMEZONE, nairobiZone.id)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            window.first.atZone(nairobiZone).toInstant().toEpochMilli())
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
            window.second.atZone(nairobiZone).toInstant().toEpochMilli())
    }
    try {
        context.startActivity(intent)
    } catch (error: ActivityNotFoundException) {
        Toast.makeText(context, R.string.calendar_unavailable, Toast.LENGTH_LONG).show()
    }
}
