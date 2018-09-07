package com.carexpenses.akhutornoy.carexpenses.base

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carexpenses.akhutornoy.carexpenses.R
import kotlinx.android.synthetic.main.fragment_lpg.*

abstract class BaseFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragmentLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun fragmentLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        handleErrors()
        handleProgressBar()
    }

    private fun handleErrors() {
        getBaseViewModel()?.showError?.observe(this, Observer { handleErrorMessage(it!!) })
    }

    protected fun handleErrorMessage(errorMessage: String) {
        Log.e("Error", errorMessage)
    }

    private fun handleProgressBar() {
        getBaseViewModel()?.showProgressLiveData?.observe(this, Observer {
            needShow ->
            if (needShow!!) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        })
    }

    protected abstract fun init()

    protected abstract fun getBaseViewModel(): BaseViewModel?

    protected fun onError(error: Throwable) {
        Log.e("TAG is NOT set yet", error.message, error)
        error.message?.let { showInfoMessage(it) }
    }

    protected fun showInfoMessage(message: String) {
        AlertDialog.Builder(requireActivity())
                .setMessage(message)
                .setNegativeButton(getString(R.string.all_ok)) { dialog, _ -> dialog.cancel() }
                .show()
    }
}