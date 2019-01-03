package com.akhutornoy.carexpenses.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        autoUnsubscribe.dispose()
    }
    protected fun showError(error: Throwable) {
        Timber.e(error)
        error.message?.run { showError.value = error.message }
    }
}