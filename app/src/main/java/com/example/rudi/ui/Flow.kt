package com.example.rudi.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rudi.ui.viewmodel.DraftViewModel
import com.example.rudi.ui.viewmodel.PickupViewModel

fun Fragment.startFlow(route: String) {
    val host = requireActivity() as MainActivity
    val models = ViewModelProvider(host)
    val draft = models[DraftViewModel::class.java]
    val pickup = models[PickupViewModel::class.java]
    draft.clear(route)
    pickup.consumeSaved()
    if (route == "pickup") pickup.prefillAddress(draft)
    host.openScreen(ItemFragment())
}
