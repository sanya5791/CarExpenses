package com.carexpenses.akhutornoy.carexpenses.di.refilllist

import com.carexpenses.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.carexpenses.akhutornoy.carexpenses.ui.list.LpgRefillListFragment
import com.carexpenses.akhutornoy.carexpenses.ui.list.PetrolRefillListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RefillListFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragmentLpg(): LpgRefillListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragmentPetroll(): PetrolRefillListFragment
}