package com.carexpenses.akhutornoy.carexpenses.domain

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface RefillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(refill: Refill)

    @Update
    fun update(refill: Refill)

    @Delete
    fun delete(refill: Refill)

    @Query("SELECT * FROM Refill WHERE createdAt == :createdAt")
    fun getByCreatedAt(createdAt: Long): Refill?

    @Query("SELECT * FROM Refill WHERE fuelType == :fuelType and createdAt BETWEEN :filterDateFrom and :filterDateTo ORDER BY createdAt DESC")
    /**
     * @param fuelType: use Refill.FuelType
     */
    fun getByFuelType(fuelType: Int, filterDateFrom: Long, filterDateTo: Long): Flowable<List<Refill>>

    @Query("SELECT * FROM Refill WHERE fuelType == :fuelType and createdAt ORDER BY createdAt DESC")
    /**
     * @param fuelType: use Refill.FuelType
     */
    fun getByFuelType(fuelType: Int): Flowable<List<Refill>>

    @Query("SELECT * FROM Refill WHERE createdAt BETWEEN :filterDateFrom and :filterDateTo ORDER BY createdAt DESC")
    fun getAll(filterDateFrom: Long, filterDateTo: Long): Flowable<List<Refill>>

    @Query("SELECT * FROM Refill WHERE createdAt ORDER BY createdAt DESC")
    fun getAll(): Flowable<List<Refill>>

    @Query("SELECT * FROM Refill WHERE createdAt < :createdAt ORDER BY createdAt DESC LIMIT 1")
    fun getPrevious(createdAt: Long): Single<Refill>
}