package com.carexpenses.akhutornoy.carexpenses.base

import android.support.v7.app.AppCompatActivity
import com.carexpenses.akhutornoy.carexpenses.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            return supportFragmentManager.popBackStack()
        }
        super.onBackPressed()
    }

    protected fun replaceFragment(fragment: BaseFragment) {
        replaceFragment(fragment, true)
    }

    protected fun replaceFragment(fragment: BaseFragment, addToBackStack: Boolean) {
        val t = supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.javaClass.name)
        if (addToBackStack) {
            t.addToBackStack(fragment.javaClass.name)
        }
        t.commit()
    }
}