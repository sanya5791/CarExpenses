package com.akhutornoy.carexpenses.ui.list.dbbackup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

internal const val BACKUP_READ_REQUEST_CODE = 42
internal const val BACKUP_WRITE_REQUEST_CODE = 43
private const val ZIP_MIME_TYPE = "application/zip"

private const val BACKUP_FILE_NAME_START = "CarExpenses-Backup-"
private const val DATE_FORMAT = "yyyy-MM-dd"

class BackupDestinationHelper {

    /**
     * Starts Android 'Storage Access Framework' to define destination Folder and
     * create backup destination 'zip' file there.
     *
     * tip: override FragmentActivity#onActivityResult and pass result into {@link #getCreateBackupFileOutputStream}
     */
    fun startCreateBackupFileOperation(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = ZIP_MIME_TYPE
        intent.putExtra(Intent.EXTRA_TITLE, getBackupFileName())
        fragment.startActivityForResult(intent, BACKUP_WRITE_REQUEST_CODE)
    }

    private fun getBackupFileName(): String {

        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return BACKUP_FILE_NAME_START + sdf.format(Date())
    }

    /**
     * Starts Android 'Storage Access Framework' to pick a 'zip' with backup there
     *
     * tip: override FragmentActivity#onActivityResult and pass result into {@link #getRestoreBackupInputStream}
     */
    fun startRestoreBackupZipFile(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = ZIP_MIME_TYPE

        fragment.startActivityForResult(intent, BACKUP_READ_REQUEST_CODE)
    }

    fun getCreateBackupFileOutputStream(context: Context, resultCode: Int, data: Intent?): OutputStream? {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val outputStream = context.contentResolver?.openOutputStream(uri)

            return outputStream
        } else {
            return null
        }
    }

    fun getRestoreBackupInputStream(context: Context, resultCode: Int, data: Intent?): InputStream? {
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            val inputStream = context.contentResolver?.openInputStream(uri)
            return inputStream
        } else {
            return null
        }
    }
}