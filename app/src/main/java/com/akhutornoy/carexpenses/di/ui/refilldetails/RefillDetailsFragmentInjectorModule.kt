package com.akhutornoy.carexpenses.di.ui.refilldetails

import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.CreateRefillDetailsFragment
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.EditRefillDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class RefillDetailsFragmentInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillDetailsFragmentModule::class])
    abstract fun createRefillDetailsFragment(): CreateRefillDetailsFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RefillDetailsFragmentModule::class])
    abstract fun editRefillDetailsFragment(): EditRefillDetailsFragment
}