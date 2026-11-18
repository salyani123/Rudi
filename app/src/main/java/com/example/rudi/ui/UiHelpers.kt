package com.example.rudi.ui

import android.R
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

fun Spinner.bindChoices(choices: List<String>, selected: String? = null, onSelected: (String) -> Unit) {
    val adapter = ArrayAdapter(context, R.layout.simple_spinner_item, choices)
    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
    this.adapter = adapter
    selected?.let {
        val index = choices.indexOf(it)
        if (index >= 0) setSelection(index)
    }
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onSelected(choices[position])
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
