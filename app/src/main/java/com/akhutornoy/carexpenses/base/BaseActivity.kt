package com.akhutornoy.carexpenses.base

import android.support.v7.app.AppCompatActivity
import com.akhutornoy.carexpenses.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            return
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
            if (it != null && it != fragment) {
                fm.beginTransaction().remove(it).commitNow()
            }
        }

        showFragment(fragment, false)
    }
}