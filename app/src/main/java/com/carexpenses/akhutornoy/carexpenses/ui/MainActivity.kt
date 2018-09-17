package com.carexpenses.akhutornoy.carexpenses.ui

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseActivity
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.base.IToolbar
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.ui.list.LpgRefillListFragment
import com.carexpenses.akhutornoy.carexpenses.ui.list.PetrolRefillListFragment
import com.carexpenses.akhutornoy.carexpenses.ui.list.RefillListFragment
import com.carexpenses.akhutornoy.carexpenses.ui.refilldetails.RefillDetailsFragment
import com.carexpenses.akhutornoy.carexpenses.ui.stubscreen.StubFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), RefillListFragment.Navigation, RefillDetailsFragment.Navigation, IToolbar {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
        if (savedInstanceState == null) {
            onLpgClicked()
        }
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
        val fragmentTag = LpgRefillListFragment::class.java.name
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)?: LpgRefillListFragment.newInstance()
        showTopFragment(fragment as BaseFragment)
    }

    private fun onPetrolClicked() {
        val fragmentTag = PetrolRefillListFragment::class.java.name
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)?: PetrolRefillListFragment.newInstance()
        showTopFragment(fragment as BaseFragment)
    }

    private fun onServiceClicked() {
        showNotImplemented()
    }

    private fun showNotImplemented() {
        showTopFragment(StubFragment.newInstance())
    }

    override fun setToolbar(toolbar: Toolbar, showHomeAsUp: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setToolbarTitle(title: Int) {
        supportActionBar?.setTitle(title)
    }

    override fun navigateToCreateNewRefill(fuelType: Refill.FuelType) {
        showFragment(RefillDetailsFragment.newInstance(fuelType))
    }

    override fun navigateToEditRefill(fuelType: Refill.FuelType, refillId: Long) {
        showFragment(RefillDetailsFragment.newInstance(fuelType, refillId))
    }

    override fun navigationFinishScreen() {
        onBackPressed()
    }

}
