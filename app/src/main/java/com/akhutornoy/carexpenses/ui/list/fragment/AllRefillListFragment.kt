package com.akhutornoy.carexpenses.ui.list.fragment

import android.view.View
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.ui.list.model.AllSummary
import com.akhutornoy.carexpenses.ui.list.viewmodel.AllRefillListViewModel
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import javax.inject.Inject

class AllRefillListFragment: BaseRefillListFragment<AllSummary>() {
    @Inject
    lateinit var allViewModel : AllRefillListViewModel

    override val viewModel: BaseRefillListViewModel<AllSummary>
        get() = allViewModel

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_all)
    }

    override val addFabVisibility: Int
        get() = View.GONE

    override val fuelTypeVisibility: Int
        get() = View.VISIBLE

    override fun getSummaryString(summary: AllSummary): String {
        return getString(R.string.refill_list_all_summary_text,
                summary.avgLpg, summary.avgPetrol,
                summary.distance, summary.money)
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(AllRefillListFragment(), FuelType.ALL)
        }
    }
}