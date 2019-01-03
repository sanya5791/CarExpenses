package com.akhutornoy.carexpenses.ui.base

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akhutornoy.carexpenses.R
import com.github.ajalt.timberkt.Timber

abstract class BaseFragment: androidx.fragment.app.Fragment() {

    abstract fun initViewModelObservers()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragmentLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun fragmentLayoutId(): Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        handleErrors()
        handleProgressBar()

        initViewModelObservers()
    }

    private fun handleErrors() {
        getBaseViewModel()?.showError?.observe(this, Observer { handleErrorMessage(it) })
    }

    protected fun handleErrorMessage(errorMessage: String?) {
        val msg = errorMessage ?: "Error message is NULL"
        Timber.e { msg }
        Toast.makeText(activity!!, msg, Toast.LENGTH_LONG).show()
    }

    private fun handleProgressBar() {
        getBaseViewModel()?.showProgressLiveData?.observe(this, Observer {
            needShow ->
            if (needShow!!) {
                getProgressBar()?.visibility = View.VISIBLE
            } else {
                getProgressBar()?.visibility = View.GONE
            }
        })
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    protected abstract fun initView()

    protected abstract fun getBaseViewModel(): BaseViewModel?

    protected abstract fun getProgressBar(): View?

    abstract fun loadData()

    protected fun onError(error: Throwable) {
        Timber.e(error)
        error.message?.let { showInfoMessage(it) }
    }

    protected fun showInfoMessage(message: String) {
        AlertDialog.Builder(requireActivity())
                .setMessage(message)
                .setNegativeButton(getString(R.string.all_ok)) { dialog, _ -> dialog.cancel() }
                .show()
    }
}