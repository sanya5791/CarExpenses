package com.carexpenses.akhutornoy.carexpenses.domain

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface RefillDao {

    @Query("SELECT * FROM Refill")
    fun getAll(): Flowable<List<Refill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(refill: Refill)

    @Update
    fun update(refill: Refill)

    @Delete
    fun delete(refill: Refill)
}