package com.akhutornoy.carexpenses.ui.list.model

import com.akhutornoy.carexpenses.data.db.Refill
import java.lang.IllegalArgumentException

enum class FuelType {
    PETROL,
    LPG,
    ALL;

    companion object {
        fun mapToDbFuelType(fuelType: FuelType): Refill.FuelType {
            return when (fuelType) {
                LPG -> Refill.FuelType.LPG
                PETROL -> Refill.FuelType.PETROL
                else -> throw IllegalArgumentException("Enum ${fuelType.name} canNOT be mapped to '${Refill.FuelType::class.java.name}")
            }
        }
    }
}