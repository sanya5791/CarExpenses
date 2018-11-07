package com.akhutornoy.carexpenses.di.refilllist

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import com.akhutornoy.carexpenses.di.scopes.FragmentScope
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.domain.db_backup_restore.BackupFilesProvider
import com.akhutornoy.carexpenses.domain.db_backup_restore.TempDbHandler
import com.akhutornoy.carexpenses.domain.db_backup_restore.Zipper
import com.akhutornoy.carexpenses.ui.list.fragment.AllRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.WriteExternalStoragePermissionHelper
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
    fun provideLpgViewModelFactory(refillDao: RefillDao, backupFilesProvider: BackupFilesProvider): ViewModelFactory {
        return ViewModelFactory(refillDao,
                backupFilesProvider,
                Zipper(),
                TempDbHandler(backupFilesProvider))
    }

    @Provides
    @FragmentScope
    fun providePathProvider(context: Context) = BackupFilesProvider(context)

    @Provides
    @FragmentScope
    fun provideWriteExternalStoragePermissionHelper() = WriteExternalStoragePermissionHelper()

    class ViewModelFactory(
            private val refillDao: RefillDao,
            private val backupFilesProvider: BackupFilesProvider,
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
                        backupFilesProvider,
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