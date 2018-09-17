package com.akhutornoy.carexpenses.ui.list.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.akhutornoy.carexpenses.base.BaseSavableViewModel
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.list.model.*
import com.akhutornoy.carexpenses.utils.DATE_TIME_FORMAT
import com.akhutornoy.carexpenses.utils.applyProgressBar
import com.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

open class RefillListViewModel(
        private val refillDao: RefillDao) : BaseSavableViewModel() {

    private lateinit var onLoadRefillsLiveData: MutableLiveData<RefillResult>

    private var filterRange: FilterDateRange = FilterDateRange()

    override fun saveInner(bundle: Bundle) {
        bundle.putParcelable(KEY_FILTER_DATE_RANGE, filterRange)
    }

    override fun restoreInner(bundle: Bundle) {
        filterRange = bundle.getParcelable(KEY_FILTER_DATE_RANGE)
    }

    fun getRefills(fuelType: FuelType): LiveData<RefillResult> {
        return getRefills(fuelType, filterRange)
    }

    fun getRefills(fuelType: FuelType, filterRange: FilterDateRange): LiveData<RefillResult> {

        val isFilterChanged = filterRange.from != this.filterRange.from
                                    || filterRange.to != this.filterRange.to
        this.filterRange = filterRange
        if (::onLoadRefillsLiveData.isInitialized
                && !isFilterChanged) {
            return onLoadRefillsLiveData
        }

        onLoadRefillsLiveData = MutableLiveData()
        autoUnsubscribe(
                getRefillsFlowable(fuelType, filterRange)
                        .map { refills ->  mapToRefillItems(refills)}
                        .subscribeOn(Schedulers.io())
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { refills -> onLoadRefillsLiveData.value = refills},
                                this::showError
                        )
        )

        return onLoadRefillsLiveData
    }

    protected  open fun getRefillsFlowable(fuelType: FuelType, filterRange: FilterDateRange): Flowable<List<Refill>> {
        val dbFuelType = FuelType.mapToDbFuelType(fuelType)
        return if (filterRange.isEmpty()) {
            refillDao.getByFuelType(dbFuelType.value)
        } else {
            refillDao.getByFuelType(
                    dbFuelType.value,
                    filterRange.from.toDate().time,
                    filterRange.to.plusDays(1).toDate().time)
        }
    }

    private fun mapToRefillItems(items: List<Refill>): RefillResult {
        var liters = 0
        var money = 0

        val refills = items.map { dbItem ->
            val date = DateTime(dbItem.createdAt).toString(DATE_TIME_FORMAT)
            liters += dbItem.litersCount
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
        return RefillResult(refills, Summary(liters, money))
    }

    companion object {
        const val KEY_FILTER_DATE_RANGE = "KEY_FILTER_DATE_RANGE"
    }
}