package com.carexpenses.akhutornoy.carexpenses.di.refilllist

import com.carexpenses.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.carexpenses.akhutornoy.carexpenses.ui.list.RefillListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RefillListFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragment(): RefillListFragment
}