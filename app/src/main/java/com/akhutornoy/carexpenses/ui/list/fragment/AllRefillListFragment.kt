package com.akhutornoy.carexpenses.ui.list.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.ui.MainActivity
import com.akhutornoy.carexpenses.ui.list.dbbackup.BACKUP_READ_REQUEST_CODE
import com.akhutornoy.carexpenses.ui.list.dbbackup.BACKUP_WRITE_REQUEST_CODE
import com.akhutornoy.carexpenses.ui.list.dbbackup.BackupDestinationHelper
import com.akhutornoy.carexpenses.ui.list.model.AllSummary
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import javax.inject.Inject

class AllRefillListFragment: BaseRefillListFragment<AllSummary>() {
    @Inject
    lateinit var allViewModel : AllRefillListViewModel

    override val viewModel: BaseRefillListViewModel<AllSummary>
        get() = allViewModel

    private val backupDestinationHelper by lazy { BackupDestinationHelper() }

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
                Observer { showRestartAppDialog()})
    }

    private fun showRestartAppDialog() {
        AlertDialog.Builder(activity!!)
                .setMessage(getString(R.string.message_restore_db_data_will_be_available_after_restart))
                .setPositiveButton(R.string.all_ok) { _, _ ->  restartApp()}
                .show()
    }

    private fun restartApp() {
        val startActivity = Intent(activity, MainActivity::class.java)
        val pendingIntentId = 123456
        val pendingIntent = PendingIntent.getActivity(activity, pendingIntentId, startActivity,
                PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
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
            R.id.action_backup_create ->  performCreateBackupDestinationFolderSearch()
            R.id.action_backup_restore -> performRestoreBackupZipFileSearch()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun performCreateBackupDestinationFolderSearch(): Boolean {
        backupDestinationHelper.startCreateBackupFileOperation(this)
        return true
    }

    private fun performRestoreBackupZipFileSearch(): Boolean {
        backupDestinationHelper.startRestoreBackupZipFile(this)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            BACKUP_WRITE_REQUEST_CODE -> createDbBackup(resultCode, data)
            BACKUP_READ_REQUEST_CODE -> restoreDbBackup(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun createDbBackup(resultCode: Int, data: Intent?) {
        backupDestinationHelper.getCreateBackupFileOutputStream(activity!!, resultCode, data)?.
                run { allViewModel.createDbBackup(this) }
    }

    private fun restoreDbBackup(resultCode: Int, data: Intent?) {
        backupDestinationHelper.getRestoreBackupInputStream(activity!!, resultCode, data)?.
            run { allViewModel.restoreDbBackup(this) }
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(AllRefillListFragment(), FuelType.ALL)
        }
    }
}