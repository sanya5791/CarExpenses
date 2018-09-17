package com.akhutornoy.carexpenses.ui.list.fragment

import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.di.refilllist.RefillListFragmentModule
import com.akhutornoy.carexpenses.ui.list.viewmodel.RefillListViewModel
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import javax.inject.Inject
import javax.inject.Named

class PetrolRefillListFragment: RefillListFragment() {
    @Inject
    @field:Named(RefillListFragmentModule.NAMED_PETROL)
    lateinit var lpgViewModel : RefillListViewModel

    override val viewModel: RefillListViewModel
        get() = lpgViewModel

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarSubtitle(R.string.title_petrol)
    }

    companion object {
        fun newInstance(): BaseFragment {
            return newInstance(PetrolRefillListFragment(), FuelType.PETROL)
        }
    }
}