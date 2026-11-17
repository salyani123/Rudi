package com.example.rudi.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rudi.data.model.PickupRequest

@Dao
interface PickupDao {
    @Insert
    suspend fun insert(request: PickupRequest): Long
    @Query("SELECT * FROM pickup_requests ORDER BY createdAt DESC")
    fun observeAll(): LiveData<List<PickupRequest>>
    @Query("SELECT * FROM pickup_requests WHERE id = :id")
    suspend fun getById(id: Long): PickupRequest?
    @Query("SELECT address FROM pickup_requests ORDER BY createdAt DESC LIMIT 1")
    suspend fun latestAddress(): String?
    @Update
    suspend fun update(request: PickupRequest): Int
    @Delete
    suspend fun delete(request: PickupRequest): Int
}
