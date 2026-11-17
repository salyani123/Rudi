package com.example.rudi.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rudi.data.model.CollectionCentre

@Dao
interface CentreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(centres: List<CollectionCentre>)
    @Query("SELECT COUNT(*) FROM collection_centres")
    suspend fun count(): Int
    @Query(
        "SELECT * FROM collection_centres WHERE city = :city " +
        "AND (:type = '' OR acceptedTypes = '' " +
        "OR instr(acceptedTypes, '|' || :type || '|') > 0) ORDER BY name"
    )
    fun observeFiltered(city: String, type: String): LiveData<List<CollectionCentre>>
    @Query("SELECT * FROM collection_centres WHERE id = :id")
    suspend fun getById(id: Long): CollectionCentre?
}
