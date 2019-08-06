package com.akhutornoy.carexpenses.ui.list.viewmodel

import androidx.lifecycle.LiveData
import com.akhutornoy.carexpenses.data.db.Refill
import com.akhutornoy.carexpenses.data.db.RefillDao
import com.akhutornoy.carexpenses.data.backup.BackupSourceHelper
import com.akhutornoy.carexpenses.data.backup.TempDbHandler
import com.akhutornoy.carexpenses.data.backup.Zipper
import com.akhutornoy.carexpenses.ui.list.model.*
import com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator.DistanceCalculator
import com.akhutornoy.carexpenses.ui.utils.DATE_TIME_FORMAT
import com.akhutornoy.carexpenses.ui.utils.FuelConsumption
import com.akhutornoy.carexpenses.ui.utils.SingleLiveEvent
import org.joda.time.DateTime
import java.io.InputStream
import java.io.OutputStream

class AllRefillListViewModel(
        private val refillDao: RefillDao,
        private val distanceCalculator: DistanceCalculator,
        private val backupSourceHelper: BackupSourceHelper,
        private val zipper: Zipper,
        private val tempDb: TempDbHandler
) : BaseRefillListViewModel<AllSummary>(refillDao) {

    private val _onBackupRestoreFinished = SingleLiveEvent<Boolean>()
    val onBackupRestoreFinished: LiveData<Boolean>
        get() = _onBackupRestoreFinished

    override fun getRefillsFromDb(fuelType: FuelType, filterRange: FilterDateRange): LiveData<List<Refill>> {
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
        val distance = distanceCalculator.getDistance(items)

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

    fun createDbBackup(outputStream: OutputStream) {
        launchBackgroundJob {
            createDbZipBackup(outputStream)
        }
    }

    fun restoreDbBackup(inputStream: InputStream) {
        launchBackgroundJob {
            restoreDbZipBackupAndCloseStream(inputStream)
            _onBackupRestoreFinished.postValue(true)
        }
    }

    private fun createDbZipBackup(outputStream: OutputStream) {
        val sourceDbFolder =
                backupSourceHelper.getDbSourceFolder()
                        ?: throw BackupFilesException("Can't Create Backup since External Storage is NOT Available")
        zipper.zipAll(sourceDbFolder, outputStream)
    }

    private fun restoreDbZipBackupAndCloseStream(inputStream: InputStream) {
        val destinationDbFolder =
                backupSourceHelper.getDbSourceFolder()
                        ?: throw BackupFilesException("Can't Create Backup since External Storage is NOT Available")

        tempDb.createTempDb(destinationDbFolder)

        try {
            zipper.unzipAll(destinationDbFolder, inputStream)
            inputStream.close()
        } catch (e: Exception) {
            tempDb.restoreTempDb(destinationDbFolder)
            showError.value = "Db restore Error."
        }
        tempDb.deleteTempDb()
    }

    class BackupFilesException(errorMessage: String) : RuntimeException(errorMessage)
}