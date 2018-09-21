package com.akhutornoy.carexpenses.ui.list.model

open class RefillResult<T> (val refills: List<RefillItem>, val summary: T, val filterRange: FilterDateRange)