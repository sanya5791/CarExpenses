package com.akhutornoy.carexpenses.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {
    private val autoUnsubscribe: CompositeDisposable = CompositeDisposable()
    val showProgressLiveData = MutableLiveData<Boolean>()
    val showError = MutableLiveData<String>()

    protected fun autoUnsubscribe(disposable: Disposable) {
        autoUnsubscribe.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        autoUnsubscribe.clear()
    }
    protected fun showError(error: Throwable) {
        Timber.e(error)
        error.message?.run { showError.value = error.message }
    }
}