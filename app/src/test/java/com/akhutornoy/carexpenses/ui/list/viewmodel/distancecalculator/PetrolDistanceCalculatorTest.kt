package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class PetrolDistanceCalculatorTest {
    private val distanceCalculator = DistanceCalculatorFactory.create(FuelType.PETROL)

    @Test
    fun getDistance_NoRefills_ReturnZero() {
        val expected = 0
        val refills = arrayListOf<Refill>()
        val result = distanceCalculator.getDistance(refills)

        assertEquals(expected, result)
    }

    @Test
    fun getDistance_OneRefill_ReturnZero() {
        val expected = 0
        val refill = Refill(
                createdAt = Date().time)
        val refills = arrayListOf(refill)
        val result = distanceCalculator.getDistance(refills)

        assertEquals(expected, result)
    }

    @Test
    fun getDistance_TwoPetrol_ReturnDistanceBetween() {
        val expected = 100

        val initDate = DateTime()
        val first = Refill(
                createdAt = initDate.minusDays(1).millis,
                currentMileage = 0)
        val second = Refill(
                createdAt = initDate.millis,
                currentMileage = 100)

        val refills = arrayListOf(first, second)
        val result = distanceCalculator.getDistance(refills)

        assertEquals(expected, result)
    }
}