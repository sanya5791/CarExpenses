package com.carexpenses.akhutornoy.carexpenses.ui.list

import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.di.refilllist.RefillListFragmentModule
import com.carexpenses.akhutornoy.carexpenses.domain.Refill.FuelType.PETROL
import javax.inject.Inject
import javax.inject.Named

class PetrolRefillListFragment: RefillListFragment() {
    @Inject
    @field:Named(RefillListFragmentModule.NAMED_PETROL)
    lateinit var lpgViewModel : RefillListViewModel

    override val viewModel: RefillListViewModel
        get() = lpgViewModel

    override protected fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_petrol)
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(PetrolRefillListFragment(), PETROL)
        }
    }
}