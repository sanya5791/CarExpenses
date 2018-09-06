package com.carexpenses.akhutornoy.carexpenses.di.app.bins

import android.content.Context
import com.carexpenses.akhutornoy.carexpenses.domain.Db
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideRefillDao(context: Context): RefillDao {
        return Db.getInstance(context).refillDao()
    }
}