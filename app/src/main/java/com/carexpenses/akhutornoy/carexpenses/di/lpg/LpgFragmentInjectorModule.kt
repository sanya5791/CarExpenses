package com.carexpenses.akhutornoy.carexpenses.di.lpg

import com.carexpenses.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.carexpenses.akhutornoy.carexpenses.ui.refilldetails.RefillDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class LpgFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [LpgFragmentModule::class])
    abstract fun lpgFragment(): RefillDetailsFragment
}