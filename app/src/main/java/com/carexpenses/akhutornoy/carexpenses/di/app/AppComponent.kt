package com.carexpenses.akhutornoy.carexpenses.di.app

import com.carexpenses.akhutornoy.carexpenses.di.app.bins.AppModule
import com.carexpenses.akhutornoy.carexpenses.di.app.bins.RoomModule
import com.carexpenses.akhutornoy.carexpenses.di.lpg.LpgFragmentInjectorModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    RoomModule::class,
    LpgFragmentInjectorModule::class
])
interface AppComponent : AndroidInjector<DaggerApplication>