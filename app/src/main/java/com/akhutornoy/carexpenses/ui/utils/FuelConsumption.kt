package com.akhutornoy.carexpenses.ui.utils

class FuelConsumption {
    companion object {
        fun calcAvgConsumption(distance: Int, liters: Int): Float {
            if(distance == 0) return 0f

            val consumption = (liters.toFloat() / distance.toFloat()) * 100
            return if(consumption >= 0) consumption
            else 0f
        }
    }
}