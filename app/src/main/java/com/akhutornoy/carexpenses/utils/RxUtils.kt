package com.akhutornoy.carexpenses.utils

import com.akhutornoy.carexpenses.base.BaseViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.applySchedulers(): Flowable<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applySchedulers(): Single<T> {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun Completable.applySchedulers(): Completable {
    return subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.applyProgressBar(progressViewModel: BaseViewModel): Single<T> {
    return doOnSubscribe {progressViewModel.showProgressLiveData.value = true}
            .doOnSuccess {progressViewModel.showProgressLiveData.value = false}
            .doOnError {progressViewModel.showProgressLiveData.value = false}
}

fun Completable.applyProgressBar(progressViewModel: BaseViewModel): Completable {
    return doOnSubscribe {progressViewModel.showProgressLiveData.value = true}
            .doOnComplete {progressViewModel.showProgressLiveData.value = false}
            .doOnError {progressViewModel.showProgressLiveData.value = false}
}

fun <T> Flowable<T>.applyProgressBar(progressViewModel: BaseViewModel): Flowable<T> {
    return doOnSubscribe {progressViewModel.showProgressLiveData.value = true}
            .doOnRequest {progressViewModel.showProgressLiveData.value = true}
            .doAfterNext {progressViewModel.showProgressLiveData.value = false}
            .doFinally {progressViewModel.showProgressLiveData.value = false}
}

