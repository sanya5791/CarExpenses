package com.akhutornoy.carexpenses.ui.list.fragment

import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.ui.base.BaseFragment
import com.akhutornoy.carexpenses.di.ui.refilllist.RefillListFragmentModule
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.model.Summary
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import javax.inject.Inject
import javax.inject.Named

class PetrolRefillListFragment: BaseRefillListFragment<Summary>() {
    @Inject
    @field:Named(RefillListFragmentModule.NAMED_PETROL)
    lateinit var petrolViewModel : RefillListViewModel

    override val viewModel: BaseRefillListViewModel<Summary>
        get() = petrolViewModel

    override val isSummaryHeightExtended = false

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_petrol)
    }

    override fun getSummaryMessage(summary: Summary): String {
        return getString(R.string.refill_list_summary_text,
                summary.avgConsumption,
                summary.distance,
                summary.liters,
                summary.money)
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(PetrolRefillListFragment(), FuelType.PETROL)
        }
    }
}