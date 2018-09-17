package com.akhutornoy.carexpenses.ui.list

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate

@Parcelize
data class FilterDateRange(val from: LocalDate = empty, val to: LocalDate = empty) : Parcelable {

    companion object {
        private val empty = LocalDate(0L)
    }

    fun isEmpty() = from == to
}