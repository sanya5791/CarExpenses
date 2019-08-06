package com.akhutornoy.carexpenses.di.data.backup

import android.content.Context
import com.akhutornoy.carexpenses.data.backup.BackupRepository
import com.akhutornoy.carexpenses.data.backup.BackupSourceHelper
import com.akhutornoy.carexpenses.data.backup.TempDbHandler
import com.akhutornoy.carexpenses.data.backup.Zipper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BackupRepositoryModule {

    @Provides
    @Singleton
    fun provideBackupRepository(
            backupSourceHelper: BackupSourceHelper,
            zipper: Zipper,
            tempDbHandler: TempDbHandler
    ) = BackupRepository(
            backupSourceHelper,
            zipper,
            tempDbHandler)

    @Provides
    @Singleton
    fun provideBackupSourceHelper(context: Context) = BackupSourceHelper(context)

    @Provides
    @Singleton
    fun provideZipper() = Zipper()

    @Provides
    @Singleton
    fun provideTempDbHandler(backupSourceHelper: BackupSourceHelper) =
            TempDbHandler(backupSourceHelper)

}