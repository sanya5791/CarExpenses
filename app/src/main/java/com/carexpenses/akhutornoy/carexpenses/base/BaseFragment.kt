package com.carexpenses.akhutornoy.carexpenses.base

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carexpenses.akhutornoy.carexpenses.Injection
import com.carexpenses.akhutornoy.carexpenses.utils.unsafeLazy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment<T : ViewModel> : Fragment() {

    abstract val viewModelClass: Class<T>

    protected val viewModel: T by unsafeLazy {
        ViewModelProviders.of(this, Injection.provideViewModelFactory(requireActivity()))
                .get(viewModelClass)
    }

    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(fragmentLayoutId(), container, false)
    }

    @LayoutRes
    protected abstract fun fragmentLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    protected abstract fun init()

    protected fun unsubscribeOnStop(disposable: Disposable) {
        this.disposable.add(disposable)
    }

    override fun onStop() {
        disposable.clear()
        super.onStop()
    }

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