package com.akhutornoy.carexpenses.ui.refilldetails.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.room.EmptyResultSetException
import com.akhutornoy.carexpenses.base.BaseViewModel
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.utils.FuelConsumption
import com.akhutornoy.carexpenses.utils.applyProgressBar
import com.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.Completable
import io.reactivex.Single

open class CreateRefillDetailsViewModel(
        private val refillDao: RefillDao) : BaseViewModel() {

    val onInsertedLiveData = MutableLiveData<Boolean>()
    val onConsumptionCalculated = MutableLiveData<Consumption>()

    private fun Int.isEmpty() = this == Refill.UNSET_INT

    fun insert(refill: Refill) {
        autoUnsubscribe(
                calcConsumption(refill)
                        .onErrorResumeNext {
                            if (isFirstDbRecordError(it))
                                Single.fromCallable { Consumption(true, 0f) }
                            else
                                throw it
                        }
                        .map { consumption ->
                            if (consumption.isCalculated) {
                                refill.consumption = consumption.consumption
                            }
                            refill
                        }
                        .flatMapCompletable { t: Refill ->  Completable.fromAction { refillDao.insert(t) } }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onInsertedLiveData.value = true },
                                this::showError)
        )
    }

    private fun isFirstDbRecordError(error: Throwable) = error is EmptyResultSetException

    fun onConsumptionRelatedDataChanged(refill: Refill) {
        autoUnsubscribe(
                calcConsumption(refill)
                        .applySchedulers()
                        .subscribe(
                                { consumption -> onConsumptionCalculated.value = consumption },
                                this::onCalcConsumptionError
                        )
        )
    }

    private fun onCalcConsumptionError(error: Throwable) {
        if(isFirstDbRecordError(error))
            onConsumptionCalculated.value = Consumption(false)
        else
            showError(error)
    }

    private fun calcConsumption(refill: Refill): Single<Consumption> {
        return when (refill.fuelType()) {
            Refill.FuelType.LPG -> calcLpgConsumption(refill)
            Refill.FuelType.PETROL -> calcPetrolConsumption(refill)
        }
    }

    private fun calcLpgConsumption(refill: Refill): Single<Consumption> {
        val canCalcLocally = !refill.litersCount.isEmpty() && !refill.lastDistance.isEmpty()
        return if (canCalcLocally) {
            calcLocally(refill)
        } else {
            Single.fromCallable { Consumption(false) }
        }
    }

    private fun calcPetrolConsumption(refill: Refill): Single<Consumption> {
        val canCalcRemotely = !refill.litersCount.isEmpty() && !refill.currentMileage.isEmpty()
                && (refill.currentMileage / 1000) >= 1

        return if (canCalcRemotely)
            calcRemotely(refill)
        else
            Single.fromCallable { Consumption(false) }
    }

    private fun calcRemotely(refill: Refill): Single<Consumption> {
        return refillDao.getPrevious(refill.createdAt)
                .map { lastRefill ->
                    Consumption(true,
                            FuelConsumption.calcAvgConsumption(refill.currentMileage - lastRefill.currentMileage, refill.litersCount))
                }
    }

    private fun calcLocally(refill: Refill): Single<Consumption> {
        return Single.fromCallable{
            val consumption = FuelConsumption.calcAvgConsumption(refill.lastDistance, refill.litersCount)
            Consumption(true, consumption)
        }
    }

    data class Consumption(val isCalculated: Boolean, val consumption: Float = 0f)
}