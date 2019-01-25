package com.akhutornoy.carexpenses.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel() {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val showProgressLiveData = MutableLiveData<Boolean>()
    val showError = MutableLiveData<String>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    protected fun launchBackgroundJob(backgroundJob: () -> Unit) = launchBackgroundJobWithErrorHandling(backgroundJob)

    protected fun launchBackgroundJob(backgroundJob: () -> Unit,
                                      errorHandler: (Throwable) -> Unit = this::showError)
            = launchBackgroundJobWithErrorHandling(backgroundJob, errorHandler)

    private fun launchBackgroundJobWithErrorHandling(
            backgroundJob: () -> Unit,
            errorHandler: (Throwable) -> Unit = this::showError): Job {

        return uiScope.launch {
            showProgressLiveData.value = true
            try {
                withContext(Dispatchers.IO) {
                    backgroundJob()
                }
            } catch (error: Throwable) {
                errorHandler(error)
            }
            showProgressLiveData.value = false
        }
    }

    protected fun showError(error: Throwable) {
        Timber.e(error)
        error.message?.run { showError.value = error.message }
    }
}