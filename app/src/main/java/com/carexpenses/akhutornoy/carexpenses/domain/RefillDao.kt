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

    @Query("SELECT * FROM Refill WHERE createdAt == :createdAt")
    fun getByCreatedAt(createdAt: Long): Refill?

    @Query("SELECT * FROM Refill WHERE fuelType == :fuelType")
            /**
             * @param fuelType: use Refill.FuelType
             */
    fun getByFuelType(fuelType: Int): Flowable<List<Refill>>
}