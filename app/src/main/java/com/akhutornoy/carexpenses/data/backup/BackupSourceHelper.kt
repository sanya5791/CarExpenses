package com.akhutornoy.carexpenses.data.backup

import android.content.Context
import com.github.ajalt.timberkt.Timber
import java.io.File

private const val PRIVATE_APP_STORAGE_FOLDER_DB_NAME = "databases"
private const val PRIVATE_APP_STORAGE_TEMP_FOLDER_DB_NAME = "temp"

class BackupSourceHelper(private val context: Context) {

    fun getDbSourceFolder(): File? {
        val dataDir = context.dataDir
        val dataBaseFolder = File(dataDir, PRIVATE_APP_STORAGE_FOLDER_DB_NAME)
        if (!dataBaseFolder.exists()) {
            Timber.e { "Can't Create Backup since Private DB folder is NOT found" }
            return null
        }
        if (!dataBaseFolder.isDirectory) {
            Timber.e { "Can't Create Backup since Private DB folder is NOT folder" }
            return null
        }

        return  dataBaseFolder
    }

    fun getDbTempFolder(): File? {
        val dbSourceFolder = getDbSourceFolder()
        val tempFolder = File(dbSourceFolder, PRIVATE_APP_STORAGE_TEMP_FOLDER_DB_NAME)

        if (!tempFolder.exists()) {
            val isTempFolderCreated = tempFolder.mkdir()
            return if (!isTempFolderCreated) {
                null
            } else {
                tempFolder
            }
        }

        if (!tempFolder.isDirectory) {
            return null
        }

        return tempFolder
    }

}