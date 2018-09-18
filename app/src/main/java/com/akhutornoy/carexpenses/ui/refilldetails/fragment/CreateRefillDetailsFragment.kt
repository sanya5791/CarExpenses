package com.akhutornoy.carexpenses.ui.refilldetails.fragment

import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.refilldetails.viewmodel.CreateRefillDetailsViewModel
import javax.inject.Inject

class CreateRefillDetailsFragment : BaseRefillDetailsFragment() {

    @Inject
    lateinit var viewModelCreate : CreateRefillDetailsViewModel

    override val createRefillDetailsViewModel: CreateRefillDetailsViewModel
        get() = viewModelCreate

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarTitle(R.string.refill_details_title_new_entry)
    }

    companion object {
        fun newInstance(fuelType: FuelType): BaseFragment {
            return CreateRefillDetailsFragment().apply { arguments = getArguments(fuelType) }
        }
    }
}
