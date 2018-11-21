package com.akhutornoy.carexpenses.base

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar

interface IToolbar {
    fun setToolbar(toolbar: Toolbar, showHomeAsUp: Boolean)
    fun setToolbarTitle(@StringRes title: Int)
    fun setToolbarSubtitle(@StringRes subTitle: Int)
}