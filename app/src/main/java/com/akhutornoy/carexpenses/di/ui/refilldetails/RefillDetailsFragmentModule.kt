package com.akhutornoy.carexpenses.di.ui.refilldetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.data.db.RefillDao
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.CreateRefillDetailsFragment
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.EditRefillDetailsFragment
import com.akhutornoy.carexpenses.ui.refilldetails.viewmodel.CreateRefillDetailsViewModel
import com.akhutornoy.carexpenses.ui.refilldetails.viewmodel.EditRefillDetailsViewModel
import dagger.Module
import dagger.Provides
import java.lang.IllegalArgumentException

@Module
class RefillDetailsFragmentModule {

    @Provides
    @FragmentScope
    fun provideCreateRefillViewModel(fragment: CreateRefillDetailsFragment,
                                     factory: RefillDetailsViewModelFactory) : CreateRefillDetailsViewModel {
        return ViewModelProviders.of(fragment, factory).get(CreateRefillDetailsViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideEditRefillViewModel(fragment: EditRefillDetailsFragment,
                                   factory: RefillDetailsViewModelFactory) : EditRefillDetailsViewModel {
        return ViewModelProviders.of(fragment, factory).get(EditRefillDetailsViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideRefillDetailsViewModelFactory(refillDao: RefillDao): RefillDetailsViewModelFactory {
        return RefillDetailsViewModelFactory(refillDao)
    }

    class RefillDetailsViewModelFactory(private val refillDao: RefillDao) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return (when (modelClass) {
                CreateRefillDetailsViewModel::class.java -> CreateRefillDetailsViewModel(refillDao)
                EditRefillDetailsViewModel::class.java -> EditRefillDetailsViewModel(refillDao)
                else -> throw IllegalArgumentException("Don't have a ViewModel for $modelClass")
            }) as T
        }
    }
}