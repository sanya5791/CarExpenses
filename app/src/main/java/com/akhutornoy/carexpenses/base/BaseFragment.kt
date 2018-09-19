package com.akhutornoy.carexpenses.base

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akhutornoy.carexpenses.R
import com.github.ajalt.timberkt.Timber

abstract class BaseFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //place here to avoid double LiveData subscription on reattach the same Fragment instance
        //todo consider move the call to initView() with support library v.28.0.0 after it will be released
        initViewModelObservers()
    }

    abstract fun initViewModelObservers()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initViewModelObservers()
        return inflater.inflate(fragmentLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun fragmentLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        handleErrors()
        handleProgressBar()
    }

    private fun handleErrors() {
        getBaseViewModel()?.showError?.observe(this, Observer { handleErrorMessage(it) })
    }

    protected fun handleErrorMessage(errorMessage: String?) {
        val msg = errorMessage ?: "Error message is NULL"
        Timber.e { msg }
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
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

    protected abstract fun initView()

    protected abstract fun getBaseViewModel(): BaseViewModel?

    protected abstract fun getProgressBar(): View?

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