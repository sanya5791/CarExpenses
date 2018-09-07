package com.carexpenses.akhutornoy.carexpenses.ui.lpg

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.carexpenses.akhutornoy.carexpenses.base.exceptions.ItemNotFoundExeption
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RefillViewModel(
        private val refillDao: RefillDao) : BaseViewModel() {

    private lateinit var onLoadByIdLiveData: MutableLiveData<Refill>
    private var onInsertedLiveData = MutableLiveData<Boolean>()

    fun insert(refill: Refill): LiveData<Boolean> {

        autoUnsubscribe(
                Completable.fromAction { refillDao.insert(refill) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { showProgressLiveData.value = true }
                        .doOnComplete { showProgressLiveData.value = false }
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
                Single.fromCallable { refillDao.getByCreatedAt(id)?: throw ItemNotFoundExeption() }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { showProgressLiveData.value = true }
                        .doAfterSuccess { showProgressLiveData.value = false }
                        .subscribe(
                                { onLoadByIdLiveData.value = it },
                                { showError.value = it.message })
        )

        return onLoadByIdLiveData
    }
}