package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.domain.Refill

class PetrolDistanceCalculator : BaseDistanceCalculator(), DistanceCalculator {
    override fun getDistance(refills: List<Refill>): Int {
        if (refills.isEmpty()) return 0

        val maxMillage = getMaxMillage(refills) ?: return 0
        val minMillage = getMinMillage(refills, maxMillage)
        return maxMillage.currentMileage -  minMillage.currentMileage
    }
}