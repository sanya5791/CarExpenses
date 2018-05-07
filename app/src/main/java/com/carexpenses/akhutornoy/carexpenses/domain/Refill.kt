package com.carexpenses.akhutornoy.carexpenses.domain

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Refill(
        @PrimaryKey
        val createdAt: Long,
        val editedAt: Long = createdAt,
        val litersCount: Int = UNSET_INT,
        val moneyCount: Int = UNSET_INT,
        val lastDistance: Int = UNSET_INT,
        val distanceMode: Int = UNSET_INT,
        val note: String = ""
) {

    fun distanceMode(): DistanceMode {
        for (value in DistanceMode.values()) {
            if (distanceMode == value.value) {
                return value
            }
        }
        throw IllegalArgumentException()
    }

    enum class DistanceMode (val value: Int) {
        CITY(0),
        HIGHWAY(1),
        MIXED(2)
    }

    companion object {
        const val UNSET_INT: Int = 0
    }
}