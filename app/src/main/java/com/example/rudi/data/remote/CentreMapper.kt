package com.example.rudi.data.remote

import com.example.rudi.data.model.CollectionCentre
import com.example.rudi.ui.viewmodel.Choices

fun CentreDto.toEntityOrNull(): CollectionCentre? {
    val cleanName = name.orEmpty().trim()
    val cleanCity = city.orEmpty().trim()
    if (id <= 0 || cleanName.isEmpty() || cleanCity !in Choices.cities) return null
    val types = acceptedTypes.orEmpty().filter { it in Choices.deviceTypes }
    return CollectionCentre(
        id = id, name = cleanName, city = cleanCity,
        address = address.orEmpty().trim(), hours = hours.orEmpty().trim(),
        requirements = requirements.orEmpty().trim(),
        acceptedTypes = if (types.isEmpty()) "" else "|" + types.joinToString("|") + "|",
        mapQuery = mapQuery.orEmpty().trim(), sourceUrl = sourceUrl.orEmpty().trim(),
        verificationNote = verificationNote.orEmpty().trim()
    )
}
