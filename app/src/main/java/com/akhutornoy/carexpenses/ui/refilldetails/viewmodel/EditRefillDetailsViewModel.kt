package com.akhutornoy.carexpenses.ui.refilldetails.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.akhutornoy.carexpenses.base.exceptions.ItemNotFoundException
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.RefillDao
import com.akhutornoy.carexpenses.utils.applyProgressBar
import com.akhutornoy.carexpenses.utils.applySchedulers
import io.reactivex.Single

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
        autoUnsubscribe(
                Single.fromCallable { refillDao.getByCreatedAt(id)?: throw ItemNotFoundException("ItemId=$id") }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onLoadByIdLiveData.value = it },
                                this::showError )
        )

        return onLoadByIdLiveData
    }

    fun delete(dbId: Long) {
        autoUnsubscribe(
                Single.fromCallable { getRefillFromDb(dbId) }
                        .doOnSuccess { refill -> refillDao.delete(refill) }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onRefillDeletedLiveData.value = true },
                                this::showError
                        )

        )
    }

    private fun getRefillFromDb(dbId: Long) =
            refillDao.getByCreatedAt(dbId)?: throw ItemNotFoundException(
                    "Can't find '${Refill::class.java.simpleName}' for id='$dbId'")
}