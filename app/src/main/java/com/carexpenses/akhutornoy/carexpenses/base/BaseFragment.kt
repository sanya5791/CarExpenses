package com.carexpenses.akhutornoy.carexpenses.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(fragmentLayoutId(), container, false)
        initViews()
        return view
    }

    protected abstract fun initViews()

    @LayoutRes
    protected abstract fun fragmentLayoutId(): Int
}