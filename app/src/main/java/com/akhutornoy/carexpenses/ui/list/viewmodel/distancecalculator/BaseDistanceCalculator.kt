package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.domain.Refill

open class BaseDistanceCalculator {
    protected fun Int.notEmpty(): Boolean = this != Refill.UNSET_INT

    protected fun getMaxMillage(refills: List<Refill>): Refill? {
        var maxMillage= refills.firstOrNull() ?: return null

        refills.forEach {
            if (maxMillage.currentMileage < it.currentMileage) {
                maxMillage = it
            }
        }
        return maxMillage
    }

    protected fun getMinMillage(refills: List<Refill>, maxMileageRefill: Refill): Refill {
        var minMillageRefill = maxMileageRefill

        refills.forEach {
            if (it.currentMileage.notEmpty()
                    && minMillageRefill.currentMileage > it.currentMileage) {
                minMillageRefill = it
            }
        }
        return minMillageRefill
    }
}