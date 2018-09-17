package com.akhutornoy.carexpenses.di.lpg

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.refilldetails.RefillDetailsFragment
import com.akhutornoy.carexpenses.ui.refilldetails.RefillDetailsViewModel
import dagger.Module
import dagger.Provides

@Module
class LpgFragmentModule {

    @Provides
    @FragmentScope
    fun provideRefillViewModel(fragment: RefillDetailsFragment,
                               factory: LpgViewModelFactory) : RefillDetailsViewModel {
        return ViewModelProviders.of(fragment, factory).get(RefillDetailsViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideLpgViewModelFactory(refillDao: RefillDao): LpgViewModelFactory {
        return LpgViewModelFactory(refillDao)
    }

    class LpgViewModelFactory(private val refillDao: RefillDao) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RefillDetailsViewModel(refillDao) as T
        }
    }
}