package com.carexpenses.akhutornoy.carexpenses.di.lpg

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import com.carexpenses.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import com.carexpenses.akhutornoy.carexpenses.ui.lpg.LpgFragment
import com.carexpenses.akhutornoy.carexpenses.ui.lpg.RefillViewModel
import dagger.Module
import dagger.Provides

@Module
class LpgFragmentModule {

    @Provides
    @FragmentScope
    fun provideRefillViewModel(fragment: LpgFragment,
                               factory: LpgViewModelFactory) : RefillViewModel {
        return ViewModelProviders.of(fragment, factory).get(RefillViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideLpgViewModelFactory(refillDao: RefillDao): LpgViewModelFactory {
        return LpgViewModelFactory(refillDao)
    }

    class LpgViewModelFactory(private val refillDao: RefillDao) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RefillViewModel(refillDao) as T
        }
    }
}