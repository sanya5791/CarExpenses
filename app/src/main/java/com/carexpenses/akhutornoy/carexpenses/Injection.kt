package com.carexpenses.akhutornoy.carexpenses

import android.content.Context
import com.carexpenses.akhutornoy.carexpenses.domain.Db
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao

object Injection {
    fun provideUserDataSource(context: Context): RefillDao {
        val database = Db.getInstance(context)
        return database.refillDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
//        val dataSource = provideUserDataSource(context)
//        return ViewModelFactory(dataSource)
        return ViewModelFactory(Db.getInstance(context))
    }
}