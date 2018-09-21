package com.akhutornoy.carexpenses.ui

import android.os.Bundle
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseActivity
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.ui.list.fragment.AllRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.BaseRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.LpgRefillListFragment
import com.akhutornoy.carexpenses.ui.list.fragment.PetrolRefillListFragment
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.BaseRefillDetailsFragment
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.CreateRefillDetailsFragment
import com.akhutornoy.carexpenses.ui.refilldetails.fragment.EditRefillDetailsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), BaseRefillListFragment.Navigation, BaseRefillDetailsFragment.Navigation {
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
            is BaseRefillListFragment<*> -> true
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
                    onAllClicked()
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

    private fun onAllClicked() {
        val fragmentTag = AllRefillListFragment::class.java.name
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)?: AllRefillListFragment.newInstance()
        showTopFragment(fragment as BaseFragment)
    }

    override fun navigateToCreateNewRefill(fuelType: FuelType) {
        showFragment(CreateRefillDetailsFragment.newInstance(fuelType))
    }

    override fun navigateToEditRefill(fuelType: FuelType, refillId: Long) {
        showFragment(EditRefillDetailsFragment.newInstance(fuelType, refillId))
    }

    override fun navigationFinishScreen() {
        onBackPressed()
    }
}
