package com.carexpenses.akhutornoy.carexpenses.ui.list

import org.joda.time.LocalDate

data class FilterDateRange(val from: LocalDate = empty, val to: LocalDate = empty) {

    companion object {
        private val empty = LocalDate(0L)
    }

    fun isEmpty() = from == to
}