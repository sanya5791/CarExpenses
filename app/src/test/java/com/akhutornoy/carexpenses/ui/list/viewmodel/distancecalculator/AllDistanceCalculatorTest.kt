package com.akhutornoy.carexpenses.ui.list.viewmodel.distancecalculator

import com.akhutornoy.carexpenses.data.db.Refill
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class AllDistanceCalculatorTest {
    private val distanceCalculator = DistanceCalculatorFactory.create(FuelType.ALL)

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

    @Test
    fun getDistance_DistanceBeforeMillageShouldBeAdded() {
        val expected = 125
        val initDate = DateTime()

        val distanceBeforeFirst = Refill(
                createdAt = initDate.minusDays(2).millis,
                lastDistance = 25)
        val first = Refill(
                createdAt = initDate.minusDays(1).millis,
                currentMileage = 0)
        val second = Refill(
                createdAt = initDate.millis,
                currentMileage = 100)

        val refills = arrayListOf(distanceBeforeFirst, first, second)
        val actual = distanceCalculator.getDistance(refills)

        assertEquals(expected, actual)
    }

    @Test
    fun getDistance_DistanceAfterMillageShouldBeAdded() {
        val expected = 125
        val initDate = DateTime()

        val first = Refill(
                createdAt = initDate.minusDays(1).millis,
                currentMileage = 0)
        val second = Refill(
                createdAt = initDate.millis,
                currentMileage = 100)
        val distanceAfterSecond = Refill(
                createdAt = initDate.minusDays(2).millis,
                lastDistance = 25)

        val refills = arrayListOf(first, second, distanceAfterSecond)
        val actual = distanceCalculator.getDistance(refills)

        assertEquals(expected, actual)
    }

    @Test
    fun getDistance_DistanceWithinMillageShouldNotBeAdded() {
        val expected = 100
        val initDate = DateTime()

        val first = Refill(
                createdAt = initDate.minusDays(2).millis,
                currentMileage = 0)
        val distanceWithinFirstAndSecond = Refill(
                createdAt = initDate.minusDays(1).millis,
                lastDistance = 25)
        val second = Refill(
                createdAt = initDate.millis,
                currentMileage = 100)

        val refills = arrayListOf(first, distanceWithinFirstAndSecond, second)
        val actual = distanceCalculator.getDistance(refills)

        assertEquals(expected, actual)
    }
}