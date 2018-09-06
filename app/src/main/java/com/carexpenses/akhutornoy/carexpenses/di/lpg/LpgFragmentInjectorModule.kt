package com.carexpenses.akhutornoy.carexpenses.di.lpg

import com.carexpenses.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.carexpenses.akhutornoy.carexpenses.ui.lpg.LpgFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LpgFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [LpgFragmentModule::class])
    abstract fun lpgFragment(): LpgFragment
}