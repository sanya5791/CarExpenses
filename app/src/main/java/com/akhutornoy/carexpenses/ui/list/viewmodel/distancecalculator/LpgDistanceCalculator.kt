package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.domain.Refill

class LpgDistanceCalculator: DistanceCalculator {
    override fun getDistance(items: List<Refill>): Int {
        var distance = 0
        items.forEach { distance += it.lastDistance }

        return distance
    }
}