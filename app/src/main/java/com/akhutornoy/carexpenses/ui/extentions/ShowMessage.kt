package com.akhutornoy.tastekeystore.utils.ui

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast

fun showSnack(view: View, @StringRes msgResId: Int) {
    showSnack(view, view.context.getString(msgResId))
}

fun showSnack(view: View, msg: String) {
    Snackbar.make(
        view,
        msg,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun showToast(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context, @StringRes msgResId: Int) {
    showToast(context, context.getString(msgResId))
}