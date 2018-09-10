package com.carexpenses.akhutornoy.carexpenses.domain

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(Refill::class)], version = 2, exportSchema = false)
abstract class Db : RoomDatabase() {
    abstract fun refillDao(): RefillDao

    companion object {
        @Volatile private var INSTANCE: Db? = null

        fun getInstance(context: Context) =
                INSTANCE?: synchronized(this) {
                    INSTANCE?: createInstance(context)
                }

        private fun createInstance(context: Context) =
                Room.databaseBuilder(context.applicationContext, Db::class.java, "CarExpenses")
                        .fallbackToDestructiveMigration()
                        .build()
    }
}