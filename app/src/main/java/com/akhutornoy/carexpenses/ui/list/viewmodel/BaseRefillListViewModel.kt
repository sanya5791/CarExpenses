package com.akhutornoy.carexpenses.ui.list.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.base.BaseSavableViewModel
import com.akhutornoy.carexpenses.ui.list.model.FilterDateRange
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.model.RefillResult
import com.github.ajalt.timberkt.Timber

abstract class BaseRefillListViewModel<T>(
        private val refillDao: RefillDao

) : BaseSavableViewModel() {

    protected var filterRange: FilterDateRange = FilterDateRange()

    private val refillArguments = MutableLiveData<RefillArguments>()

    val onLoadRefillsLiveData: LiveData<RefillResult<T>> =
            Transformations.switchMap(refillArguments, ::getRefillsResult)

    override fun saveInner(bundle: Bundle) {
        bundle.putParcelable(KEY_FILTER_DATE_RANGE, filterRange)
    }

    override fun restoreInner(bundle: Bundle) {
        filterRange = bundle.getParcelable(KEY_FILTER_DATE_RANGE) ?: FilterDateRange()
    }

    fun loadRefills(fuelType: FuelType) {
        loadRefills(fuelType, filterRange)
    }

    fun loadRefills(fuelType: FuelType, filterRange: FilterDateRange) {
        if (canUseCachedData(filterRange)) {
            return
        }
        startRefillLoading(fuelType, filterRange)
    }

    private fun canUseCachedData(newFilterRange: FilterDateRange): Boolean {
        val isFilterChanged = newFilterRange.from != this.filterRange.from
                || newFilterRange.to != this.filterRange.to
        this.filterRange = newFilterRange
        val refills = onLoadRefillsLiveData.value

        return refills != null && !isFilterChanged
    }

    private fun startRefillLoading(fuelType: FuelType, filterRange: FilterDateRange) {
        refillArguments.value = RefillArguments(fuelType, filterRange)
    }

    private fun getRefillsResult(arguments: RefillArguments): LiveData<RefillResult<T>> {
        val refills = getRefillsFromDb(arguments.fuelType, arguments.filterRange)
        return Transformations.map(refills, this::mapToRefillResult)
    }

    protected open fun getRefillsFromDb(fuelType: FuelType, filterRange: FilterDateRange): LiveData<List<Refill>> {
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

    private data class RefillArguments(val fuelType: FuelType, val filterRange: FilterDateRange)

    companion object {
        const val KEY_FILTER_DATE_RANGE = "KEY_FILTER_DATE_RANGE"
    }
}