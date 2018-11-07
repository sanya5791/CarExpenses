package com.akhutornoy.carexpenses.ui.list.viewmodel

import android.arch.lifecycle.LiveData
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.domain.db_backup_restore.BackupFilesProvider
import com.akhutornoy.carexpenses.domain.db_backup_restore.TempDbHandler
import com.akhutornoy.carexpenses.domain.db_backup_restore.Zipper
import com.akhutornoy.carexpenses.ui.list.fragment.AllRefillListFragment.BackupOperation
import com.akhutornoy.carexpenses.ui.list.model.*
import com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator.DistanceCalculator
import com.akhutornoy.carexpenses.utils.*
import io.reactivex.Completable
import io.reactivex.Flowable
import org.joda.time.DateTime

class AllRefillListViewModel(
        private val refillDao: RefillDao,
        private val distanceCalculator: DistanceCalculator,
        private val backupFilesProvider: BackupFilesProvider,
        private val zipper: Zipper,
        private val tempDb: TempDbHandler
) : BaseRefillListViewModel<AllSummary>(refillDao) {

    private val _onBackupRestoreFinished = SingleLiveEvent<BackupOperation>()
    val onBackupRestoreFinished: LiveData<BackupOperation>
        get() = _onBackupRestoreFinished

    override fun getRefillsFlowable(fuelType: FuelType, filterRange: FilterDateRange): Flowable<List<Refill>> {
        return if (filterRange.isEmpty()) {
            refillDao.getAll()
        } else {
            refillDao.getAll(
                    filterRange.from.toDate().time,
                    filterRange.to.plusDays(1).toDate().time)
        }
    }

    override fun mapToRefillResult(items: List<Refill>): RefillResult<AllSummary> {
        var lpgLiters = 0
        var petrolLiters = 0
        var money = 0
        var distance = distanceCalculator.getDistance(items)

        val refills = items.map { dbItem ->
            val date = DateTime(dbItem.createdAt).toString(DATE_TIME_FORMAT)
            when (dbItem.fuelType()) {
                Refill.FuelType.LPG -> lpgLiters += dbItem.litersCount
                Refill.FuelType.PETROL -> petrolLiters += dbItem.litersCount
            }
            money += dbItem.moneyCount

            RefillItem(
                    dbId = dbItem.createdAt,
                    consumption = dbItem.consumption,
                    date = date,
                    litersCount = dbItem.litersCount,
                    trafficMode = dbItem.trafficMode().name,
                    fuelType = Refill.FuelType.valueOf(dbItem.fuelType).name,
                    isNoteAvailable = dbItem.note != Refill.UNSET_STR
            )
        }
        return RefillResult(
                refills,
                AllSummary(
                        FuelConsumption.calcAvgConsumption(distance, lpgLiters).toInt(),
                        FuelConsumption.calcAvgConsumption(distance, petrolLiters).toInt(),
                        distance,
                        money),
                filterRange)
    }

    fun createDbBackup() {
        autoUnsubscribe(
                Completable.fromAction { createDbZipBackup() }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .doFinally { _onBackupRestoreFinished.value = BackupOperation.BACKUP }
                        .subscribe(
                                { },
                                this::showError
                        )
        )
    }

    fun restoreDbBackup() {
        autoUnsubscribe(
                Completable.fromAction { restoreDbZipBackup() }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .doFinally { _onBackupRestoreFinished.value = BackupOperation.RESTORE }
                        .subscribe(
                                { },
                                this::showError
                        )
        )
    }

    private fun createDbZipBackup() {
        val sourceDbFolder =
                backupFilesProvider.getDbSourceFolder()
                        ?: throw DefinePathException("Can't Create Backup since External Storage is NOT Available")

        val backupDir =
                backupFilesProvider.getBackupFolder()
                        ?: throw DefinePathException("Can't Create Backup since External Storage is NOT Available")

        val zipFile =
                backupFilesProvider.createBackupZipFileAndSaveOldBackupZip(backupDir)
                        ?: throw DefinePathException("Can't Create Backup since destinations ZIP file is NOT created")

        zipper.zipAll(sourceDbFolder, zipFile)
    }

    private fun restoreDbZipBackup() {
        val destinationDbFolder =
                backupFilesProvider.getDbSourceFolder()
                        ?: throw DefinePathException("Can't Create Backup since External Storage is NOT Available")

        val zippedBackupFile =
                backupFilesProvider.getBackupDbZipFile()
                        ?: throw DefinePathException("DB backup NOT found")

        tempDb.createTempDb(destinationDbFolder)

        try {
            zipper.unzipAll(destinationDbFolder, zippedBackupFile)
        } catch (e: Exception) {
            tempDb.restoreTempDb(destinationDbFolder)
            showError.value = "Db restore Error."
        }
        tempDb.deleteTempDb()
    }

    class DefinePathException(errorMessage: String) : RuntimeException(errorMessage)
}