package com.akhutornoy.carexpenses.di.refilllist

import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.ui.list.AllRefillListFragment
import com.akhutornoy.carexpenses.ui.list.LpgRefillListFragment
import com.akhutornoy.carexpenses.ui.list.PetrolRefillListFragment
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

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragmentAll(): AllRefillListFragment
}