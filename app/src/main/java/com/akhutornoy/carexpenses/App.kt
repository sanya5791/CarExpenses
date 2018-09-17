package com.akhutornoy.carexpenses

import com.akhutornoy.carexpenses.di.app.DaggerAppComponent
import com.akhutornoy.carexpenses.di.app.bins.AppModule
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class App : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}