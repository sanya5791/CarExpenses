package com.akhutornoy.carexpenses.ui.refilldetails

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.room.EmptyResultSetException
import com.akhutornoy.carexpenses.base.BaseViewModel
import com.akhutornoy.carexpenses.base.exceptions.ItemNotFoundException
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.utils.applyProgressBar
import com.akhutornoy.carexpenses.utils.applySchedulers
import com.github.ajalt.timberkt.Timber
import io.reactivex.Completable
import io.reactivex.Single

class RefillDetailsViewModel(
        private val refillDao: RefillDao) : BaseViewModel() {

    private lateinit var onLoadByIdLiveData: MutableLiveData<Refill>
    private val onInsertedLiveData = MutableLiveData<Boolean>()
    private val onRefillDeletedLiveData = MutableLiveData<Boolean>()
    val onConsumptionCalculated = MutableLiveData<Consumption>()

    private fun Int.isEmpty() = this == Refill.UNSET_INT

    fun insert(refill: Refill): LiveData<Boolean> {

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
                        }.flatMapCompletable { t: Refill ->  Completable.fromAction { refillDao.insert(t) } }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onInsertedLiveData.value = true },
                                { showError.value = it.message })
        )

        return onInsertedLiveData
    }

    private fun isFirstDbRecordError(error: Throwable) = error is EmptyResultSetException

    fun getById(id: Long): LiveData<Refill> {
        if (::onLoadByIdLiveData.isInitialized) {
            return onLoadByIdLiveData
        }

        onLoadByIdLiveData = MutableLiveData()
        autoUnsubscribe(
                Single.fromCallable { refillDao.getByCreatedAt(id)?: throw ItemNotFoundException("ItemId=$id") }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onLoadByIdLiveData.value = it },
                                this::showError )
        )

        return onLoadByIdLiveData
    }

    fun delete(dbId: Long): LiveData<Boolean> {
        autoUnsubscribe(
                Single.fromCallable { getRefillFromDb(dbId) }
                        .doOnSuccess { refill -> refillDao.delete(refill) }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onRefillDeletedLiveData.value = true },
                                { Timber.e(it) }
                        )

        )
        return onRefillDeletedLiveData
    }

    private fun getRefillFromDb(dbId: Long) =
            refillDao.getByCreatedAt(dbId)?: throw ItemNotFoundException(
                    "Can't find '${Refill::class.java.simpleName}' for id='$dbId'")

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
        val canCalcLocally = !refill.litersCount.isEmpty() && !refill.lastDistance.isEmpty()
        if (canCalcLocally) {
            return calcLocally(refill)
        }

        val canCalcRemotely = !refill.litersCount.isEmpty() && !refill.currentMileage.isEmpty()
                && (refill.currentMileage / 1000) >= 1

        return if (canCalcRemotely)
            calcRemotely(refill)
        else
            Single.fromCallable { Consumption(false) }
    }

    private fun calcRemotely(refill: Refill): Single<Consumption> {
        return refillDao.getPrevious(refill.createdAt)
                .map { lastRefill ->  Consumption(true,
                        calcConsumption(refill.currentMileage - lastRefill.currentMileage, refill.litersCount)) }
    }

    private fun calcLocally(refill: Refill): Single<Consumption> {
        return Single.fromCallable{
            val consumption = calcConsumption(refill.lastDistance, refill.litersCount)
            Consumption(true, consumption)
        }
    }

    private fun calcConsumption(distance: Int, liters: Int): Float {
        if(distance == 0) return 0f

        val consumption = (liters.toFloat() / distance.toFloat()) * 100
        return if(consumption >= 0) consumption
        else 0f
    }

    data class Consumption(val isCalculated: Boolean, val consumption: Float = 0f)
}