package com.akhutornoy.carexpenses.ui.list.viewmodel

import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.ui.list.model.RefillItem
import com.akhutornoy.carexpenses.ui.list.model.RefillResult
import com.akhutornoy.carexpenses.ui.list.model.Summary
import com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator.DistanceCalculator
import com.akhutornoy.carexpenses.utils.DATE_TIME_FORMAT
import com.akhutornoy.carexpenses.utils.FuelConsumption
import org.joda.time.DateTime

class RefillListViewModel(
        refillDao: RefillDao,
        private val distanceCalculator: DistanceCalculator
) : BaseRefillListViewModel<Summary>(refillDao) {

    override fun mapToRefillResult(items: List<Refill>): RefillResult<Summary> {
        var liters = 0
        var money = 0
        var distance = distanceCalculator.getDistance(items)

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
        return RefillResult(
                refills,
                Summary(liters, money, FuelConsumption.calcAvgConsumption(distance, liters).toInt(), distance),
                filterRange)
    }
}