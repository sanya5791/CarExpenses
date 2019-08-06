package com.akhutornoy.carexpenses.di.ui.refilllist

import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.ui.list.fragment.AllRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.LpgRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.PetrolRefillListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RefillListFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragmentLpg(): LpgRefillListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragmentPetrol(): PetrolRefillListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillListFragmentModule::class])
    abstract fun fragmentAll(): AllRefillListFragment

}