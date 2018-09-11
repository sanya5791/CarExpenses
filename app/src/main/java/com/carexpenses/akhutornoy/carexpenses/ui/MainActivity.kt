package com.carexpenses.akhutornoy.carexpenses.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseActivity
import com.carexpenses.akhutornoy.carexpenses.base.IToolbar
import com.carexpenses.akhutornoy.carexpenses.ui.list.RefillListFragment
import com.carexpenses.akhutornoy.carexpenses.ui.lpg.LpgFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), RefillListFragment.Navigation, LpgFragment.Navigation, IToolbar {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
        onLpgClicked()
    }

    override fun onBackPressed() {
        if (isTopFragmentShown()) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun isTopFragmentShown(): Boolean {
        val shownFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        return when (shownFragment) {
            is RefillListFragment -> true
            else -> false
        }
    }

    private fun initListeners() {
        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_lpg -> {
                    onLpgClicked()
                    true
                }
                R.id.navigation_petrol -> {
                    onPetrolClicked()
                    true
                }
                R.id.navigation_service -> {
                    onServiceClicked()
                    true
                }
                else -> false
            }
        }
    }

    private fun onLpgClicked() {
        setNotImplementedVisible(false)
        val fragmentTag = RefillListFragment::class.java.simpleName
        val lpgFragment = supportFragmentManager.findFragmentByTag(fragmentTag)?: RefillListFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, lpgFragment, fragmentTag)
                .commit()
    }

    private fun onPetrolClicked() {
        showNotImplemented()
    }

    private fun onServiceClicked() {
        showNotImplemented()
    }

    private fun showNotImplemented() {
        setNotImplementedVisible(true)
        val fragment = supportFragmentManager.findFragmentByTag(LpgFragment::class.java.simpleName)

        fragment ?: return

        supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
    }

    private fun setNotImplementedVisible(isVisible: Boolean) {
        val visibilityState = if (isVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        notImplementedTextView.visibility = visibilityState
    }

    override fun setToolbar(toolbar: Toolbar, showHomeAsUp: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
//        toolbar.navigationIcon?.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP)
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setToolbarTitle(title: Int) {
        supportActionBar?.setTitle(title)
    }

    override fun navigateToCreateNewRefill() {
        replaceFragment(LpgFragment.newInstance())
    }

    override fun navigateToEditRefill(refillId: Long) {
        replaceFragment(LpgFragment.newInstance(refillId))
    }

    override fun navigationFinishScreen() {
        onBackPressed()
    }

}
