package com.akhutornoy.carexpenses.ui.list.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.akhutornoy.carexpenses.R
import com.github.ajalt.timberkt.Timber

private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: Int = 111

class WriteExternalStoragePermissionHelper {

    fun askPermission(fragment: Fragment) {
        fun askPermission() =
                fragment.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(fragment.activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.activity!!,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(fragment.activity!!)
                        .setMessage("To data create backup 'Write External Storage' permission is required" )
                        .setPositiveButton(fragment.getString(R.string.grand)) { _, _ ->  askPermission() }
                        .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
            } else {
                // No explanation needed, we can request the permission.
                askPermission()
            }
        } else {
            Timber.d { "Permission has already been granted" }
        }
    }

    fun canHandleRequestCode(requestCode: Int) =
        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE == requestCode

    fun isGranted(grantResults: IntArray): Boolean {
        return (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
    }
}