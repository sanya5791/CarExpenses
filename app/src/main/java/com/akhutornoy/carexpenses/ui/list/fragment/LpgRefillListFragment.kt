package com.akhutornoy.carexpenses.ui.list.fragment

import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.di.refilllist.RefillListFragmentModule
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.model.Summary
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import javax.inject.Inject
import javax.inject.Named

class LpgRefillListFragment: BaseRefillListFragment<Summary>() {
    @Inject
    @field:Named(RefillListFragmentModule.NAMED_LPG)
    lateinit var lpgViewModel : RefillListViewModel

    override val viewModel: BaseRefillListViewModel<Summary>
        get() = lpgViewModel

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_lpg)
    }

    override fun getSummaryString(summary: Summary): String {
        return getString(R.string.refill_list_summary_text,
                summary.avgConsumption,
                summary.distance,
                summary.liters,
                summary.money)
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(LpgRefillListFragment(), FuelType.LPG)
        }
    }
}