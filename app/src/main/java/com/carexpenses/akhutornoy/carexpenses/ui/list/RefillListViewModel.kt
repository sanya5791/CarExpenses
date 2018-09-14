package com.carexpenses.akhutornoy.carexpenses.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.carexpenses.akhutornoy.carexpenses.base.BaseSavableViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import com.carexpenses.akhutornoy.carexpenses.ui.list.model.RefillItem
import com.carexpenses.akhutornoy.carexpenses.ui.list.model.RefillResult
import com.carexpenses.akhutornoy.carexpenses.ui.list.model.Summary
import com.carexpenses.akhutornoy.carexpenses.utils.DATE_TIME_FORMAT
import com.carexpenses.akhutornoy.carexpenses.utils.applyProgressBar
import com.carexpenses.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

class RefillListViewModel(
        private val refillDao: RefillDao) : BaseSavableViewModel() {

    private lateinit var onLoadRefillsLiveData: MutableLiveData<RefillResult>

    private var filterRange: FilterDateRange = FilterDateRange()

    override fun saveInner(bundle: Bundle) {
        bundle.putParcelable(KEY_FILTER_DATE_RANGE, filterRange)
    }

    override fun restoreInner(bundle: Bundle) {
        filterRange = bundle.getParcelable(KEY_FILTER_DATE_RANGE)
    }

    fun getRefills(fuelType: Refill.FuelType): LiveData<RefillResult> {
        return getRefills(fuelType, filterRange)
    }

    fun getRefills(fuelType: Refill.FuelType, filterRange: FilterDateRange): LiveData<RefillResult> {

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

    private fun getRefillsFlowable(fuelType: Refill.FuelType, filterRange: FilterDateRange): Flowable<List<Refill>> {
        return if (filterRange.isEmpty()) {
            refillDao.getByFuelType(fuelType.value)
        } else {
            refillDao.getByFuelType(
                    fuelType.value,
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
                    isNoteAvailable = dbItem.note != Refill.UNSET_STR
            )
        }
        return RefillResult(refills, Summary(liters, money))
    }

    companion object {
        const val KEY_FILTER_DATE_RANGE = "KEY_FILTER_DATE_RANGE"
    }
}