package com.akhutornoy.carexpenses.ui.list.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.akhutornoy.carexpenses.base.BaseSavableViewModel
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.list.model.FilterDateRange
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.model.RefillResult
import com.akhutornoy.carexpenses.utils.applyProgressBar
import com.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

abstract class BaseRefillListViewModel<T> (
        private val refillDao: RefillDao

) : BaseSavableViewModel() {

    private var _onLoadRefillsLiveData = MutableLiveData<RefillResult<T>>()
    val onLoadRefillsLiveData: LiveData<RefillResult<T>>
        get() = _onLoadRefillsLiveData

    protected var filterRange: FilterDateRange = FilterDateRange()

    override fun saveInner(bundle: Bundle) {
        bundle.putParcelable(KEY_FILTER_DATE_RANGE, filterRange)
    }

    override fun restoreInner(bundle: Bundle) {
        filterRange = bundle.getParcelable(KEY_FILTER_DATE_RANGE)?: FilterDateRange()
    }

    fun getRefills(fuelType: FuelType) {
        getRefills(fuelType, filterRange)
    }

    fun getRefills(fuelType: FuelType, filterRange: FilterDateRange) {
        if (canUseRefillsLiveDta(filterRange)) {
            _onLoadRefillsLiveData.value = onLoadRefillsLiveData.value
            return
        }

        autoUnsubscribe(
                getRefillsFlowable(fuelType, filterRange)
                        .map { refills ->  mapToRefillResult(refills)}
                        .subscribeOn(Schedulers.io())
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { refillResult ->
                                    _onLoadRefillsLiveData.value = refillResult },
                                this::showError
                        )
        )
    }

    private fun canUseRefillsLiveDta(newFilterRange: FilterDateRange): Boolean {
        val isFilterChanged = newFilterRange.from != this.filterRange.from
                || newFilterRange.to != this.filterRange.to
        this.filterRange = newFilterRange
        val refills = onLoadRefillsLiveData.value

        return refills != null && !isFilterChanged
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

    protected abstract fun mapToRefillResult(items: List<Refill>): RefillResult<T>

    companion object {
        const val KEY_FILTER_DATE_RANGE = "KEY_FILTER_DATE_RANGE"
    }
}