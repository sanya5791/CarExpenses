package com.carexpenses.akhutornoy.carexpenses.ui.list

import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import java.lang.IllegalArgumentException

enum class FuelType {
    PETROL,
    LPG,
    ALL;

    companion object {
        fun mapToDbFuelType(fuelType: FuelType): Refill.FuelType {
            return when (fuelType) {
                FuelType.LPG -> Refill.FuelType.LPG
                FuelType.PETROL -> Refill.FuelType.PETROL
                else -> throw IllegalArgumentException("Enum ${fuelType.name} canNOT be mapped to '${Refill.FuelType::class.java.name}")
            }
        }
    }
}