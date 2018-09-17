package com.akhutornoy.carexpenses.ui.list.fragment

import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.di.refilllist.RefillListFragmentModule
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import javax.inject.Inject
import javax.inject.Named

class LpgRefillListFragment: RefillListFragment() {
    @Inject
    @field:Named(RefillListFragmentModule.NAMED_LPG)
    lateinit var lpgViewModel : RefillListViewModel

    override val viewModel: RefillListViewModel
        get() = lpgViewModel

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_lpg)
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(LpgRefillListFragment(), FuelType.LPG)
        }
    }
}