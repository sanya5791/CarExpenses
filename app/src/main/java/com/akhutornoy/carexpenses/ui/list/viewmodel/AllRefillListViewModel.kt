package com.akhutornoy.carexpenses.ui.list.viewmodel

import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.list.model.*
import com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator.DistanceCalculator
import com.akhutornoy.carexpenses.utils.DATE_TIME_FORMAT
import com.akhutornoy.carexpenses.utils.FuelConsumption
import io.reactivex.Flowable
import org.joda.time.DateTime

class AllRefillListViewModel(
        private val refillDao: RefillDao,
        private val distanceCalculator: DistanceCalculator
) : BaseRefillListViewModel<AllSummary>(refillDao) {

    override fun getRefillsFlowable(fuelType: FuelType, filterRange: FilterDateRange): Flowable<List<Refill>> {
        return if (filterRange.isEmpty()) {
            refillDao.getAll()
        } else {
            refillDao.getAll(
                    filterRange.from.toDate().time,
                    filterRange.to.plusDays(1).toDate().time)
        }
    }

    override fun mapToRefillResult(items: List<Refill>): RefillResult<AllSummary> {
        var lpgLiters = 0
        var petrolLiters = 0
        var money = 0
        var distance = distanceCalculator.getDistance(items)

        val refills = items.map { dbItem ->
            val date = DateTime(dbItem.createdAt).toString(DATE_TIME_FORMAT)
            when (dbItem.fuelType()) {
                Refill.FuelType.LPG -> lpgLiters += dbItem.litersCount
                Refill.FuelType.PETROL -> petrolLiters += dbItem.litersCount
            }
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
        return RefillResult(
                refills,
                AllSummary(
                        FuelConsumption.calcAvgConsumption(distance, lpgLiters).toInt(),
                        FuelConsumption.calcAvgConsumption(distance, petrolLiters).toInt(),
                        distance,
                        money),
                filterRange)
    }
}