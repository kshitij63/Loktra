package com.loktra_assign.loktra

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by user on 4/24/2018.
 */
@Entity
data class LocationObject(@PrimaryKey var timeStamp:Long,var latitude:Double,var longitude:Double)
