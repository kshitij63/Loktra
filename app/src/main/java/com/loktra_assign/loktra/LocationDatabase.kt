package com.loktra_assign.loktra

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by user on 4/24/2018.
 */
@Database(entities = arrayOf(LocationObject::class), version = 1)
abstract class LocationDatabase : RoomDatabase() {

    companion object {
        var locationDatabase: LocationDatabase? = null

        fun getLocationDatabse(context: Context): LocationDatabase {
            if (locationDatabase == null) {
                locationDatabase = Room.databaseBuilder(context
                        , LocationDatabase::class.java, "location.db")
                        .allowMainThreadQueries()
                        .build()
            }

            return locationDatabase!!
        }
    }

    abstract fun getLocationDao(): LocationDao

}