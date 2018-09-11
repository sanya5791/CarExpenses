package com.carexpenses.akhutornoy.carexpenses.ui.lpg

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.carexpenses.akhutornoy.carexpenses.base.exceptions.ItemNotFoundException
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import com.carexpenses.akhutornoy.carexpenses.utils.applyProgressBar
import com.carexpenses.akhutornoy.carexpenses.utils.applySchedulers
import com.github.ajalt.timberkt.Timber
import io.reactivex.Completable
import io.reactivex.Single

class RefillViewModel(
        private val refillDao: RefillDao) : BaseViewModel() {

    private lateinit var onLoadByIdLiveData: MutableLiveData<Refill>
    private val onInsertedLiveData = MutableLiveData<Boolean>()
    private val onRefillDeletedLiveData = MutableLiveData<Boolean>()

    fun insert(refill: Refill): LiveData<Boolean> {

        autoUnsubscribe(
                Completable.fromAction { refillDao.insert(refill) }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onInsertedLiveData.value = true },
                                { showError.value = it.message })
        )

        return onInsertedLiveData
    }

    fun getById(id: Long): LiveData<Refill> {
        if (::onLoadByIdLiveData.isInitialized) {
            return onLoadByIdLiveData
        }

        onLoadByIdLiveData = MutableLiveData()
        autoUnsubscribe(
                Single.fromCallable { refillDao.getByCreatedAt(id)?: throw ItemNotFoundException() }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onLoadByIdLiveData.value = it },
                                { showError.value = it.message })
        )

        return onLoadByIdLiveData
    }

    fun delete(dbId: Long): LiveData<Boolean> {
        autoUnsubscribe(
                Single.fromCallable { getRefillFromDb(dbId) }
                        .doOnSuccess { refill -> refillDao.delete(refill) }
                        .applySchedulers()
                        .applyProgressBar(this)
                        .subscribe(
                                { onRefillDeletedLiveData.value = true },
                                { Timber.e(it) }
                        )

        )
        return onRefillDeletedLiveData
    }

    private fun getRefillFromDb(dbId: Long) =
            refillDao.getByCreatedAt(dbId)?: throw ItemNotFoundException(
                    "Can't find '${Refill::class.java.simpleName}' for id='$dbId'")
}