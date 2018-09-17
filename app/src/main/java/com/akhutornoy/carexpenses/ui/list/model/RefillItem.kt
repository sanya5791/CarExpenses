package com.akhutornoy.carexpenses.ui.list.model

data class RefillItem(
        val dbId: Long,
        val date: String,
        val litersCount: Int,
        val consumption: Float,
        val trafficMode: String,
        val fuelType: String,
        val isNoteAvailable: Boolean
        )