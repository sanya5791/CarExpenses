package com.carexpenses.akhutornoy.carexpenses.base

import android.support.annotation.StringRes
import android.support.v7.widget.Toolbar

interface IToolbar {
    fun setToolbar(toolbar: Toolbar, showHomeAsUp: Boolean)
    fun setToolbarTitle(@StringRes title: Int)
    fun setToolbarSubtitle(@StringRes subTitle: Int)
}