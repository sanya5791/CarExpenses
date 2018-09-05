package com.carexpenses.akhutornoy.carexpenses

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.RefillDao
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable

class RefillViewModel(
        private val refillDao: RefillDao) : ViewModel() {

    private val disposable = CompositeDisposable()

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }

    val insertLiveData = MediatorLiveData<Boolean>().apply {  }

    fun insert(refill: Refill): Completable {
        return Completable.fromAction({ refillDao.insert(refill) })
    }

    fun getById(id: Long): Single<Refill> {
        return Single.fromCallable({ refillDao.getByCreatedAt(id)?: throw ItemNotFoundExeption() })
    }
}