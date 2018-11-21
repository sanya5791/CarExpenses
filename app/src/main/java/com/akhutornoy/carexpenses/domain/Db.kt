package com.akhutornoy.carexpenses.domain

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
                Room.databaseBuilder(context.applicationContext, Db::class.java, "CarExpenses.db")
                        .fallbackToDestructiveMigration()
                        .build()
    }
}