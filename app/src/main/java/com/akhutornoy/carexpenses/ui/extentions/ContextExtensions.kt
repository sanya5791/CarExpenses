package com.akhutornoy.carexpenses.ui.extentions

import android.content.Context
import androidx.annotation.StringRes
import android.widget.Toast

fun Context.showToast(msg: String) =
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

fun Context.showToast(@StringRes msgResId: Int) =
    showToast(this.getString(msgResId))