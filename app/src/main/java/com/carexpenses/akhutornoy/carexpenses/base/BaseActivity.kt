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

    protected fun showFragment(fragment: BaseFragment) {
        showFragment(fragment, true)
    }

    protected fun showFragment(fragment: BaseFragment, addToBackStack: Boolean) {
        val tag = fragment.javaClass.name
        val t = supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
        if (addToBackStack) {
            t.addToBackStack(tag)
        }
        t.commit()
    }

    protected fun showTopFragment(fragment: BaseFragment) {
        val fm = supportFragmentManager
        fm.fragments.forEach {
            if (it == fragment) return@forEach
            fm.beginTransaction().remove(it).commit()
        }

        showFragment(fragment, false)
    }
}