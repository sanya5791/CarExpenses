package com.akhutornoy.carexpenses.di.refilllist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.list.dbbackup.BackupSourceHelper
import com.akhutornoy.carexpenses.ui.list.dbbackup.TempDbHandler
import com.akhutornoy.carexpenses.ui.list.dbbackup.Zipper
import com.akhutornoy.carexpenses.ui.list.fragment.AllRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.LpgRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.PetrolRefillListFragment
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator.DistanceCalculator
import com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator.DistanceCalculatorFactory
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
        factory.distanceCalculator = DistanceCalculatorFactory.create(FuelType.LPG)
        return ViewModelProviders.of(fragment, factory).get(RefillListViewModel::class.java)
    }

    @Provides
    @Named(NAMED_PETROL)
    @FragmentScope
    fun providePetrolRefillListViewModel(fragment: PetrolRefillListFragment,
                                         factory: ViewModelFactory) : RefillListViewModel {
        factory.distanceCalculator = DistanceCalculatorFactory.create(FuelType.PETROL)
        return ViewModelProviders.of(fragment, factory).get(RefillListViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideAllRefillListViewModel(fragment: AllRefillListFragment,
                                      factory: ViewModelFactory) : AllRefillListViewModel {
        factory.distanceCalculator = DistanceCalculatorFactory.create(FuelType.ALL)
        return ViewModelProviders.of(fragment, factory).get(AllRefillListViewModel::class.java)
    }

    @Provides
    @FragmentScope
    fun provideLpgViewModelFactory(refillDao: RefillDao, backupSourceHelper: BackupSourceHelper): ViewModelFactory {
        return ViewModelFactory(refillDao,
                backupSourceHelper,
                Zipper(),
                TempDbHandler(backupSourceHelper))
    }

    @Provides
    @FragmentScope
    fun providePathProvider(context: Context) = BackupSourceHelper(context)

    class ViewModelFactory(
            private val refillDao: RefillDao,
            private val backupSourceHelper: BackupSourceHelper,
            private val zipper: Zipper,
            private val tempDbHandler: TempDbHandler
    ) : ViewModelProvider.Factory {

        lateinit var distanceCalculator: DistanceCalculator

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return (when (modelClass) {
                AllRefillListViewModel::class.java -> AllRefillListViewModel(
                        refillDao,
                        distanceCalculator,
                        backupSourceHelper,
                        zipper,
                        tempDbHandler)
                else -> RefillListViewModel(refillDao, distanceCalculator)
            }) as T
        }
    }

    companion object {
        const val NAMED_LPG = "lpg"
        const val NAMED_PETROL = "petrol"
    }
}