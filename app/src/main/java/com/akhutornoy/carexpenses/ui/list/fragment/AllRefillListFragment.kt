package com.akhutornoy.carexpenses.ui.list.fragment

import android.view.View
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import javax.inject.Inject

class AllRefillListFragment: RefillListFragment() {
    @Inject
    lateinit var allViewModel : AllRefillListViewModel

    override val viewModel: RefillListViewModel
        get() = allViewModel

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_all)
    }

    override val addFabVisibility: Int
        get() = View.GONE

    override val fuelTypeVisibility: Int
        get() = View.VISIBLE

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(AllRefillListFragment(), FuelType.ALL)
        }
    }
}