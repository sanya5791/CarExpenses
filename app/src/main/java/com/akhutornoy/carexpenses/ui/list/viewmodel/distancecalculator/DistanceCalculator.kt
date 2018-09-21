package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.domain.Refill

interface DistanceCalculator {
    fun getDistance(refills: List<Refill>): Int
}