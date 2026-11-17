package com.example.rudi.ui

import androidx.fragment.app.Fragment

fun Fragment.startFlow(route: String) {
    (activity as? MainActivity)?.openScreen(route)
}
