package com.akhutornoy.carexpenses.ui.list.dbbackup

import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.github.ajalt.timberkt.Timber
import java.io.File

class TempDbHandler(private val backupSourceHelper: BackupSourceHelper) {

    fun createTempDb(destinationDbFolder: File) {
        val tempFolder = backupSourceHelper.getDbTempFolder()
                ?: throw AllRefillListViewModel.BackupFilesException("Can't restore DB backup since 'temp' folder is NOT created")

        destinationDbFolder.listFiles().forEach { it.renameTo(File(tempFolder, it.name)) }
    }

    fun restoreTempDb(destinationDbFolder: File) {
        Timber.e { "restoreTempDb(): restoring from 'temp' data" }
        val tempFolder =
                backupSourceHelper.getDbTempFolder()
                        ?: throw AllRefillListViewModel.BackupFilesException("Can't restore DB backup since 'temp' folder is NOT created")

        tempFolder.listFiles().forEach { it.renameTo(File(destinationDbFolder, it.name)) }
        Timber.e { "restoreTempDb(): 'temp' data is restored!!!" }
    }

    fun deleteTempDb() {
        val tempFolder = backupSourceHelper.getDbTempFolder()
                ?: throw AllRefillListViewModel.BackupFilesException("Can't restore DB backup since 'temp' folder is NOT created")
        tempFolder.deleteRecursively()
    }
}