package com.carexpenses.akhutornoy.carexpenses

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.carexpenses.akhutornoy.carexpenses.domain.Db

class ViewModelFactory (private val dataSource: Db) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RefillViewModel::class.java)) {
            return RefillViewModel(dataSource.refillDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}