package com.loktra_assign.loktra

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

/**
 * Created by user on 4/24/2018.
 */
@Dao
interface LocationDao {

    @Query("select * from LocationObject order by timeStamp desc ")
    fun getAlldata(): LiveData<List<LocationObject>>


    @Insert(onConflict = REPLACE)
    fun insertLocation(locationObject: LocationObject)

    @Query("delete from LocationObject")
    fun deleteAllData()
}