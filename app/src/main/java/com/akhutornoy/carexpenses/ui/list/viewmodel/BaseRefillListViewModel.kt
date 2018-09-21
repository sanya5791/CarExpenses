package com.akhutornoy.carexpenses.ui.list.viewmodel

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

    var onLoadRefillsLiveData = MutableLiveData<RefillResult<T>>()

    protected var filterRange: FilterDateRange = FilterDateRange()

    override fun saveInner(bundle: Bundle) {
        bundle.putParcelable(KEY_FILTER_DATE_RANGE, filterRange)
    }

    override fun restoreInner(bundle: Bundle) {
        filterRange = bundle.getParcelable(KEY_FILTER_DATE_RANGE)?: FilterDateRange()
    }

    fun getRefills(fuelType: FuelType) {
        return getRefills(fuelType, filterRange)
    }

    fun getRefills(fuelType: FuelType, filterRange: FilterDateRange) {

        val isFilterChanged = filterRange.from != this.filterRange.from
                                    || filterRange.to != this.filterRange.to
        this.filterRange = filterRange

        if (onLoadRefillsLiveData.value != null
            && !isFilterChanged) {
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
                                    onLoadRefillsLiveData.value = refillResult },
                                this::showError
                        )
        )
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