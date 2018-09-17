package com.carexpenses.akhutornoy.carexpenses.ui.list

import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.di.refilllist.RefillListFragmentModule
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
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