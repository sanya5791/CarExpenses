package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.ui.list.model.FuelType

class DistanceCalculatorFactory {
    companion object {
        fun create(fuelType: FuelType): DistanceCalculator {
            return when (fuelType) {
                FuelType.LPG -> LpgDistanceCalculator()
                FuelType.PETROL -> PetrolDistanceCalculator()
                FuelType.ALL -> AllDistanceCalculator()
            }
        }
    }
}