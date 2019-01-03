package com.akhutornoy.carexpenses.ui.base

import androidx.appcompat.widget.Toolbar

class BaseToolbar(
        private val activity: BaseActivity
): IToolbar {

    override fun setToolbar(toolbar: Toolbar, showHomeAsUp: Boolean) {
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }

    override fun setToolbarTitle(title: Int) {
        activity.supportActionBar?.setTitle(title)
    }

    override fun setToolbarSubtitle(subTitle: Int) {
        activity.supportActionBar?.setSubtitle(subTitle)
    }
}