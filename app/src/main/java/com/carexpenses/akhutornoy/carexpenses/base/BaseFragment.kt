package com.carexpenses.akhutornoy.carexpenses.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragmentLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun fragmentLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    protected abstract fun initViews()

    protected fun onError(error: Throwable) {
        Log.e("TAG is NOT set yet", error.message, error)
        error.message?.let { showInfoMessage(it) }
    }

    protected fun showInfoMessage(message: String) {
        AlertDialog.Builder(requireActivity())
                .setMessage(message)
                .setNegativeButton("Ok", { dialog, _ -> dialog.cancel() })
                .show()
    }
}