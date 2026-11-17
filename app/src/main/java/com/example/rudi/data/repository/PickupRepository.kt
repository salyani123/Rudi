package com.example.rudi.data.repository

import com.example.rudi.data.local.PickupDao
import com.example.rudi.data.model.PickupRequest

class PickupRepository(private val dao: PickupDao) {
    val requests = dao.observeAll()
    suspend fun get(id: Long) = dao.getById(id)
    suspend fun latestAddress() = dao.latestAddress()
    suspend fun save(request: PickupRequest): Long {
        if (request.id == 0L) return dao.insert(request)
        check(dao.update(request) == 1) { "Request no longer exists" }
        return request.id
    }
    suspend fun delete(request: PickupRequest) {
        check(dao.delete(request) == 1) { "Request no longer exists" }
    }
}
