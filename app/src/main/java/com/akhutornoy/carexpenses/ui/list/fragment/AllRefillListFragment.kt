package com.akhutornoy.carexpenses.ui.list.fragment

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.ui.MainActivity
import com.akhutornoy.carexpenses.ui.list.model.AllSummary
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import com.akhutornoy.tastekeystore.utils.ui.showSnack
import javax.inject.Inject

class AllRefillListFragment: BaseRefillListFragment<AllSummary>() {
    @Inject
    lateinit var allViewModel : AllRefillListViewModel

    @Inject
    lateinit var permissionHelper: WriteExternalStoragePermissionHelper

    private var backupOperation = BackupOperation.FINISHED

    override val viewModel: BaseRefillListViewModel<AllSummary>
        get() = allViewModel

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_all)
    }

    override val addFabVisibility: Int
        get() = View.GONE

    override val fuelTypeVisibility: Int
        get() = View.VISIBLE

    override fun initViewModelObservers() {
        super.initViewModelObservers()
        allViewModel.onBackupRestoreFinished.observe(this,
                Observer { finishedBackupOperation ->  onFinishedBackupOperation(finishedBackupOperation)})
    }

    private fun onFinishedBackupOperation(finishedBackupOperation: BackupOperation?) {
        backupOperation = when(finishedBackupOperation){
            BackupOperation.BACKUP -> BackupOperation.FINISHED
            BackupOperation.RESTORE -> {
                showRestartAppDialog()

                BackupOperation.FINISHED
            }
            else -> throw IllegalArgumentException("Not supported operation '$finishedBackupOperation' in this method")
        }
    }

    private fun showRestartAppDialog() {
        AlertDialog.Builder(activity!!)
                .setMessage("Restored data will appear after Application restart")
                .setPositiveButton(R.string.all_ok) { _, _ ->  restartApp()}
                .show()
    }

    private fun restartApp() {
        val mStartActivity = Intent(activity, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }

    override fun getSummaryString(summary: AllSummary): String {
        return getString(R.string.refill_list_all_summary_text,
                summary.avgLpg, summary.avgPetrol,
                summary.distance, summary.money)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.findItem(R.id.action_more)?.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_backup_create -> onBackupCreateClicked()
            R.id.action_backup_restore -> onBackupRestoreClicked()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onBackupCreateClicked(): Boolean {
        backupOperation = BackupOperation.BACKUP
        if (isWriteExternalStoragePermissionGranted()) {
            permissionHelper.askPermission(this)
        } else {
            createDbBackup()
        }

        return true
    }

    private fun onBackupRestoreClicked(): Boolean {
        backupOperation = BackupOperation.RESTORE
        if (isWriteExternalStoragePermissionGranted()) {
            permissionHelper.askPermission(this)
        } else {
            restoreDbBackup()
        }

        return true
    }

    private fun isWriteExternalStoragePermissionGranted() =
            (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(permissionHelper.canHandleRequestCode(requestCode)) {
            if(permissionHelper.isGranted(grantResults)) {
                onWriteExternalStoragePermissionGranted()
            } else {
                showSnack(view!!, "Can't create backup withot WRITE_EXTERNAL_STORAGE_PERMISSION")
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun onWriteExternalStoragePermissionGranted() {
        when (backupOperation) {
            BackupOperation.BACKUP -> createDbBackup()
            BackupOperation.RESTORE -> restoreDbBackup()
            else -> throw IllegalArgumentException("Not supported backupOperation '$backupOperation' in this method")
        }
    }

    private fun createDbBackup() {
        allViewModel.createDbBackup()
    }

    private fun restoreDbBackup() {
        allViewModel.restoreDbBackup()
    }

    enum class BackupOperation {BACKUP, RESTORE, FINISHED}

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(AllRefillListFragment(), FuelType.ALL)
        }
    }
}