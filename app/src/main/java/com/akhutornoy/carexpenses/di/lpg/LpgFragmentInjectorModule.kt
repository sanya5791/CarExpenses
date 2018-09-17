package com.akhutornoy.carexpenses.di.lpg

import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.ui.refilldetails.RefillDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LpgFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [LpgFragmentModule::class])
    abstract fun lpgFragment(): RefillDetailsFragment
}