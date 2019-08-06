package com.akhutornoy.carexpenses.data.backup

import com.akhutornoy.carexpenses.data.backup.exception.BackupFilesException
import java.io.InputStream
import java.io.OutputStream

class BackupRepository(
        private val backupSourceHelper: BackupSourceHelper,
        private val zipper: Zipper,
        private val tempDb: TempDbHandler
) {

    fun createDbZipBackup(outputStream: OutputStream) {
        val sourceDbFolder =
                backupSourceHelper.getDbSourceFolder()
                        ?: throw BackupFilesException("Can't Create Backup since External Storage is NOT Available")
        zipper.zipAll(sourceDbFolder, outputStream)
    }

    fun restoreDbZipBackupAndCloseStream(inputStream: InputStream) {
        val destinationDbFolder =
                backupSourceHelper.getDbSourceFolder()
                        ?: throw BackupFilesException("Can't Create Backup since External Storage is NOT Available")

        tempDb.createTempDb(destinationDbFolder)

        try {
            zipper.unzipAll(destinationDbFolder, inputStream)
            inputStream.close()
        } catch (e: Exception) {
            tempDb.restoreTempDb(destinationDbFolder)
            throw BackupFilesException("Db restore Error.")
        }
        tempDb.deleteTempDb()
    }

}