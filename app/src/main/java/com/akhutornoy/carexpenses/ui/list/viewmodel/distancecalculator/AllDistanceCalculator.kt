package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.data.db.Refill

class AllDistanceCalculator : BaseDistanceCalculator(), DistanceCalculator {
    override fun getDistance(refills: List<Refill>): Int {
        val maxMillageRefill = getMaxMillage(refills) ?: return 0
        val minMillageRefill = getMinMillage(refills, maxMillageRefill)

        val distanceBeforeMinMillage = getDistanceBefore(refills, minMillageRefill)
        val distanceAfterMaxMillage = getDistanceAfter(refills, maxMillageRefill)

        val firstMillage = minMillageRefill.currentMileage - distanceBeforeMinMillage
        val lastMillage = maxMillageRefill.currentMileage + distanceAfterMaxMillage

        return lastMillage - firstMillage
    }

    private fun getDistanceBefore(refills: List<Refill>, minMillage: Refill): Int {
        var distance = 0
        fun isDistanceBefore(refill: Refill) = refill.createdAt < minMillage.createdAt
        refills.asSequence()
                .filter { isDistanceBefore(it) }
                .filter { it.lastDistance.notEmpty() }
                .toList()
                .forEach { distance += it.lastDistance }
        return distance
    }

    private fun getDistanceAfter(refills: List<Refill>, maxMillageRefill: Refill): Int {
        var distance = 0
        fun isDistanceAfter(refill: Refill) = refill.createdAt > maxMillageRefill.createdAt
        refills.asSequence()
                .filter { isDistanceAfter(it) }
                .filter { it.lastDistance.notEmpty() }
                .toList()
                .forEach { distance += it.lastDistance }

        return distance
    }
}