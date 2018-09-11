package com.carexpenses.akhutornoy.carexpenses.ui.list.recyclerview

data class RefillItem(
        val dbId: Long,
        val date: String,
        val litersCount: Int,
        val consumption: Int,
        val trafficMode: String,
        val isNoteAvailable: Boolean
        )