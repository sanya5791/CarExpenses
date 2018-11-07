package com.akhutornoy.carexpenses.domain.db_backup_restore

import android.content.Context
import android.os.Environment
import com.github.ajalt.timberkt.Timber
import java.io.File

private const val PRIVATE_APP_STORAGE_FOLDER_DB_NAME = "databases"
private const val PRIVATE_APP_STORAGE_TEMP_FOLDER_DB_NAME = "temp"
private const val EXT_STORAGE_BACKUP_FOLDER_NAME = "Backup"

private const val BACKUP_ZIP_FILE_NAME = "AllRefillsDb.zip"
private const val OLD_BACKUP_ZIP_FILE_NAME = "AllRefillsDb.zip.old"

class BackupFilesProvider(private val context: Context) {
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

    /* Checks if external storage is available for read and write */
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun getBackupFolder(): File? {
        if (!isExternalStorageWritable()) {
            Timber.e { "Can't Create Backup since External Storage is NOT Available" }
            return null
        }

        val backupFolder = getPublicBackupStorageFolder(EXT_STORAGE_BACKUP_FOLDER_NAME)
        if (backupFolder == null) {
            Timber.e { "Can't create backup folder" }
            return null
        }

        if (!backupFolder.exists()) {
            Timber.d { "${backupFolder.name} NOT exists - creating" }
            val isDirCreated = backupFolder.mkdir()
            Timber.d { "${backupFolder.name} isDirCreated=$isDirCreated" }
        }

        if (!backupFolder.exists()) {
            Timber.e { "${backupFolder.name} is NOT created" }
            return null
        }

        return backupFolder
    }

    private fun getPublicBackupStorageFolder(dirName: String): File? {
        // Get the directory for the user's public pictures directory.
        val fileDir = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), dirName)
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                Timber.e { "Directory NOT created" }
            }
        }
        if (!fileDir.isDirectory) {
            Timber.e { "Can't create destination folder '$EXT_STORAGE_BACKUP_FOLDER_NAME' since file with the same name already exists" }
            return null
        }
        return fileDir
    }

    fun createBackupZipFileAndSaveOldBackupZip(sourceDbFolder: File): File? {
        var zipFile = File(sourceDbFolder, BACKUP_ZIP_FILE_NAME)
        if (zipFile.exists()) {
            val renamedFile = File(sourceDbFolder, OLD_BACKUP_ZIP_FILE_NAME)
            zipFile.renameTo(renamedFile)
            zipFile = File(sourceDbFolder, BACKUP_ZIP_FILE_NAME)
        }
        val isZipFileCreated = zipFile.createNewFile()
        if (!isZipFileCreated) {
            return null
        }
        return zipFile
    }

    fun getBackupDbZipFile(): File? {
        val backupFolder = getBackupFolder() ?: return null

        val zipFiles = backupFolder.listFiles()
                .asSequence()
                .sortedByDescending { file -> file.lastModified() }
                .toList()

        return if (zipFiles.isEmpty()) {
            null
        } else {
            zipFiles.first()
        }
    }

    fun getTempDbFolder(): File? {
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