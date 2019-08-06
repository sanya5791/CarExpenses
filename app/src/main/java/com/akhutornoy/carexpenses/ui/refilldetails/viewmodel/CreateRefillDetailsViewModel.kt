package com.akhutornoy.carexpenses.ui.refilldetails.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.room.EmptyResultSetException
import com.akhutornoy.carexpenses.data.db.Refill
import com.akhutornoy.carexpenses.data.db.RefillDao
import com.akhutornoy.carexpenses.ui.base.BaseViewModel
import com.akhutornoy.carexpenses.ui.utils.FuelConsumption

open class CreateRefillDetailsViewModel(
        private val refillDao: RefillDao) : BaseViewModel() {

    val onInsertedLiveData = MutableLiveData<Boolean>()
    val onConsumptionCalculated = MutableLiveData<Consumption>()

    private fun Int.isEmpty() = this == Refill.UNSET_INT

    fun insert(refill: Refill) {
        launchBackgroundJob {
            val consumption = calcConsumption(refill)
            if (consumption.isCalculated) {
                refill.consumption = consumption.consumption
            }
            refillDao.insert(refill)
            onInsertedLiveData.postValue(true)
        }
    }

    private fun isFirstDbRecordError(error: Throwable) = error is EmptyResultSetException

    fun onConsumptionRelatedDataChanged(refill: Refill) {
        val backgroundJob = { onConsumptionCalculated.postValue(calcConsumption(refill)) }
        launchBackgroundJob(
                backgroundJob,
                this::onCalcConsumptionError
        )
    }

    private fun onCalcConsumptionError(error: Throwable) {
        if (isFirstDbRecordError(error))
            onConsumptionCalculated.value = Consumption(false)
        else
            showError(error)
    }

    private fun calcConsumption(refill: Refill): Consumption {
        return when (refill.fuelType()) {
            Refill.FuelType.LPG -> calcLpgConsumption(refill)
            Refill.FuelType.PETROL -> calcPetrolConsumption(refill)
        }
    }

    private fun Refill.canCalcLocally() = !this.litersCount.isEmpty() && !this.lastDistance.isEmpty()

    private fun Refill.canCalcRemotely() = !this.litersCount.isEmpty() && !this.currentMileage.isEmpty()
            && (this.currentMileage / 1000) >= 1

    private fun calcLpgConsumption(refill: Refill): Consumption {
        return if (refill.canCalcLocally()) {
            calcLocally(refill)
        } else {
            Consumption(false)
        }
    }

    private fun calcPetrolConsumption(refill: Refill): Consumption {
        return if (refill.canCalcRemotely())
            calcRemotely(refill)
        else
            Consumption(false)
    }

    private fun calcRemotely(refill: Refill): Consumption {
        val lastRefill = refillDao.getPrevious(refill.createdAt)
        val avgConsumption = FuelConsumption.calcAvgConsumption(refill.currentMileage - lastRefill.currentMileage, refill.litersCount)
        return Consumption(true, avgConsumption)
    }

    private fun calcLocally(refill: Refill): Consumption {
        val consumption = FuelConsumption.calcAvgConsumption(refill.lastDistance, refill.litersCount)
        return Consumption(true, consumption)
    }

    data class Consumption(val isCalculated: Boolean, val consumption: Float = 0f)
}