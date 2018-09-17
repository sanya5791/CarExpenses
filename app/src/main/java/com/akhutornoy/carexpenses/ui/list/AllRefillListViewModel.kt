package com.akhutornoy.carexpenses.ui.list

import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import io.reactivex.Flowable

class AllRefillListViewModel(
        private val refillDao: RefillDao) : RefillListViewModel(refillDao) {

    override fun getRefillsFlowable(fuelType: FuelType, filterRange: FilterDateRange): Flowable<List<Refill>> {
        return if (filterRange.isEmpty()) {
            refillDao.getAll()
        } else {
            refillDao.getAll(
                    filterRange.from.toDate().time,
                    filterRange.to.plusDays(1).toDate().time)
        }
    }
}