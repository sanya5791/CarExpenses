package com.akhutornoy.carexpenses.di.app

import com.akhutornoy.carexpenses.di.app.bins.AppModule
import com.akhutornoy.carexpenses.di.app.bins.RoomModule
import com.akhutornoy.carexpenses.di.lpg.LpgFragmentInjectorModule
import com.akhutornoy.carexpenses.di.refilllist.RefillListFragmentInjectorModule
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
    LpgFragmentInjectorModule::class,
    RefillListFragmentInjectorModule::class
])
interface AppComponent : AndroidInjector<DaggerApplication>