package com.carexpenses.akhutornoy.carexpenses.di.app.bins

import android.content.Context
import com.carexpenses.akhutornoy.carexpenses.App
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val app: App) {

    private val context: Context = app.applicationContext

    @Provides
    fun provideApp() = app

    @Provides
    fun provideContext() = context
}