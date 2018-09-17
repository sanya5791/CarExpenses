package com.akhutornoy.carexpenses.di.refilllist

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.list.fragment.AllRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.LpgRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.PetrolRefillListFragment
import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class RefillListFragmentModule {

    @Provides
    @Named(NAMED_LPG)
    @FragmentScope
    fun provideLpgRefillListViewModel(fragment: LpgRefillListFragment,
                                      factory: ViewModelFactory) : RefillListViewModel {
        return ViewModelProviders.of(fragment, factory).get(RefillListViewModel::class.java)
    }

    @Provides
    @Named(NAMED_PETROL)
    @FragmentScope
    fun providePetrolRefillListViewModel(fragment: PetrolRefillListFragment,
                                         factory: ViewModelFactory) : RefillListViewModel {
        return ViewModelProviders.of(fragment, factory).get(RefillListViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideAllRefillListViewModel(fragment: AllRefillListFragment,
                                      factory: ViewModelFactory) : AllRefillListViewModel {
        return ViewModelProviders.of(fragment, factory).get(AllRefillListViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideLpgViewModelFactory(refillDao: RefillDao): ViewModelFactory {
        return ViewModelFactory(refillDao)
    }

    class ViewModelFactory(private val refillDao: RefillDao) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return (when (modelClass) {
                AllRefillListViewModel::class.java -> AllRefillListViewModel(refillDao)
                else -> RefillListViewModel(refillDao)
            }) as T
        }
    }

    companion object {
        const val NAMED_LPG = "lpg"
        const val NAMED_PETROL = "petrol"
    }
}