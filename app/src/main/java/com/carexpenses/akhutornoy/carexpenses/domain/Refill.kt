package com.carexpenses.akhutornoy.carexpenses.domain

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Refill(
        @PrimaryKey
        val createdAt: Long,
        val editedAt: Long = createdAt,
        val fuelType:  Int = UNSET_INT,
        val litersCount: Int = UNSET_INT,
        val moneyCount: Int = UNSET_INT,
        val lastDistance: Int = UNSET_INT,
        val trafficMode: Int = TrafficMode.CITY.value,
        val note: String = UNSET_STR
) {

    fun fuelType(): FuelType {
        for (value in FuelType.values()) {
            if (fuelType == value.value) {
                return value
            }
        }
        throw IllegalArgumentException("Can't find '${FuelType::class.java.simpleName}' for value=$fuelType")
    }

    fun trafficMode(): TrafficMode {
        for (value in TrafficMode.values()) {
            if (trafficMode == value.value) {
                return value
            }
        }
        throw IllegalArgumentException()
    }

    enum class FuelType (val value: Int) {
        PETROL(0),
        LPG(1),
    }

    enum class TrafficMode (val value: Int) {
        CITY(0),
        HIGHWAY(1),
        MIXED(2)
    }

    companion object {
        const val UNSET_INT: Int = -1
        const val UNSET_STR: String = ""
    }
}