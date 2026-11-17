package com.example.rudi.data.repository

import com.example.rudi.data.local.CentreDao
import com.example.rudi.data.remote.CentreApi
import com.example.rudi.data.remote.toEntityOrNull

class CentreRepository(private val dao: CentreDao, private val api: CentreApi) {
    fun observe(city: String, type: String) = dao.observeFiltered(city, type)
    suspend fun seedIfEmpty() {
        if (dao.count() == 0) dao.upsertAll(DefaultCentres.all)
    }
    // Throws on network, HTTP or JSON errors. The ViewModel shows a notice.
    suspend fun refresh(): Int {
        val rows = api.centres().mapNotNull { it.toEntityOrNull() }
        if (rows.isNotEmpty()) dao.upsertAll(rows)
        return rows.size
    }
}
