package com.akhutornoy.carexpenses.ui.refilldetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.akhutornoy.carexpenses.data.db.Refill
import com.akhutornoy.carexpenses.data.db.RefillDao
import com.akhutornoy.carexpenses.data.db.exceptions.ItemNotFoundException

class EditRefillDetailsViewModel(
        private val refillDao: RefillDao
) : CreateRefillDetailsViewModel(refillDao) {

    private lateinit var onLoadByIdLiveData: MutableLiveData<Refill>
    val onRefillDeletedLiveData = MutableLiveData<Boolean>()

    fun getById(id: Long): LiveData<Refill> {
        if (::onLoadByIdLiveData.isInitialized) {
            return onLoadByIdLiveData
        }

        onLoadByIdLiveData = MutableLiveData()
        launchBackgroundJob {
            val refill = refillDao.getByCreatedAt(id) ?: throw ItemNotFoundException("ItemId=$id")
            onLoadByIdLiveData.postValue(refill)
        }

        return onLoadByIdLiveData
    }

    fun delete(dbId: Long) {
        launchBackgroundJob {
            val refill = getRefillFromDb(dbId)
            refillDao.delete(refill)

            onRefillDeletedLiveData.postValue(true)
        }
    }

    private fun getRefillFromDb(dbId: Long) =
            refillDao.getByCreatedAt(dbId)?: throw ItemNotFoundException(
                    "Can't find '${Refill::class.java.simpleName}' for id='$dbId'")
}