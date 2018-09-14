package com.carexpenses.akhutornoy.carexpenses.ui.list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.carexpenses.akhutornoy.carexpenses.base.BaseSavableViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import com.carexpenses.akhutornoy.carexpenses.ui.list.recyclerview.RefillItem
import com.carexpenses.akhutornoy.carexpenses.utils.DATE_TIME_FORMAT
import com.carexpenses.akhutornoy.carexpenses.utils.applyProgressBar
import com.carexpenses.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime

class RefillListViewModel(
        private val refillDao: RefillDao) : BaseSavableViewModel() {

    private lateinit var onLoadRefillsLiveData: MutableLiveData<List<RefillItem>>

    private var filterRange: FilterDateRange = FilterDateRange()

    override fun saveInner(bundle: Bundle) {
        bundle.putParcelable(KEY_FILTER_DATE_RANGE, filterRange)
    }

    override fun restoreInner(bundle: Bundle) {
        filterRange = bundle.getParcelable(KEY_FILTER_DATE_RANGE)
    }

    fun getRefills(fuelType: Refill.FuelType): LiveData<List<RefillItem>> {
        return getRefills(fuelType, filterRange)
    }

    fun getRefills(fuelType: Refill.FuelType, filterRange: FilterDateRange): LiveData<List<RefillItem>> {

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

    private fun mapToRefillItems(items: List<Refill>): List<RefillItem> {

        return items.map { dbItem ->
            val date = DateTime(dbItem.createdAt).toString(DATE_TIME_FORMAT)
            RefillItem(
                    dbId = dbItem.createdAt,
                    consumption = dbItem.consumption,
                    date = date,
                    litersCount = dbItem.litersCount,
                    trafficMode = dbItem.trafficMode().name,
                    isNoteAvailable = dbItem.note != Refill.UNSET_STR
            )
        }
    }

    companion object {
        const val KEY_FILTER_DATE_RANGE = "KEY_FILTER_DATE_RANGE"
    }
}