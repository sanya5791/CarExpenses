package com.carexpenses.akhutornoy.carexpenses.di.refilllist

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import com.carexpenses.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import com.carexpenses.akhutornoy.carexpenses.ui.list.RefillListFragment
import com.carexpenses.akhutornoy.carexpenses.ui.list.RefillListViewModel
import dagger.Module
import dagger.Provides

@Module
class RefillListFragmentModule {

    @Provides
    @FragmentScope
    fun provideRefillListViewModel(fragment: RefillListFragment,
                                   factory: ViewModelFactory) : RefillListViewModel {
        return ViewModelProviders.of(fragment, factory).get(RefillListViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideLpgViewModelFactory(refillDao: RefillDao): ViewModelFactory {
        return ViewModelFactory(refillDao)
    }

    class ViewModelFactory(private val refillDao: RefillDao) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RefillListViewModel(refillDao) as T
        }
    }
}