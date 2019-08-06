package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.data.db.Refill

interface DistanceCalculator {
    fun getDistance(refills: List<Refill>): Int
}