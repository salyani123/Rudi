package com.example.rudi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rudi.data.model.CollectionCentre
import com.example.rudi.data.model.PickupRequest

@Database(
    entities = [PickupRequest::class, CollectionCentre::class],
    version = 1,
    exportSchema = false
)
abstract class RudiDatabase : RoomDatabase() {
    abstract fun pickupDao(): PickupDao
    abstract fun centreDao(): CentreDao
    companion object {
        @Volatile private var instance: RudiDatabase? = null
        fun get(context: Context): RudiDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext, RudiDatabase::class.java, "rudi.db"
                ).build().also { instance = it }
            }
    }
}
