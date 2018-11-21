package com.akhutornoy.carexpenses.ui.refilldetails.fragment

import androidx.lifecycle.Observer
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.refilldetails.viewmodel.CreateRefillDetailsViewModel
import com.akhutornoy.carexpenses.ui.refilldetails.viewmodel.EditRefillDetailsViewModel
import kotlinx.android.synthetic.main.fragment_refill_details.*
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject

class EditRefillDetailsFragment : BaseRefillDetailsFragment() {
    @Inject
    lateinit var viewModel : EditRefillDetailsViewModel

    override val createRefillDetailsViewModel: CreateRefillDetailsViewModel
        get() = viewModel

    private val argRefillId: Long by lazy { getRefillIdFromArg() }

    private fun getRefillIdFromArg(): Long {
        if (arguments == null || !arguments!!.containsKey(ARG_REFILL_ID)) {
            throw IllegalArgumentException("Fragment '${this.javaClass.name}' should be instantiated with arg='$ARG_REFILL_ID'")
        }

        return arguments!!.getLong(ARG_REFILL_ID)
    }

    override fun initViewModelObservers() {
        super.initViewModelObservers()
        viewModel.onRefillDeletedLiveData.observe(this, Observer { isRemoved ->
            when (isRemoved) {
                true -> navigationCallback.navigationFinishScreen()
            }
        })
    }

    override fun loadData() {
        loadFromDb(argRefillId)
    }

    override fun initToolbar() {
        super.initToolbar()
        toolbar.setToolbarTitle(R.string.refill_details_title)
    }

    override fun getRefillItemCreatedAt() = argRefillId

    override fun getRefillItemEditedAt() = Date().time

    private fun loadFromDb(refillId: Long) {
        viewModel.getById(refillId)
                .observe(this, Observer { showRefill(it!!) })
    }

    private fun showRefill(refill: Refill) {
        et_liters.setText(refill.litersCount.toString())
        et_current_mileage.setText(refill.currentMileage.toString())
        et_money.setText(refill.moneyCount.toString())
        rg_distance_mode.check(getRadioButtonId(refill.trafficMode()))
        et_note.setText(refill.note)
        use_note_check_box.isChecked = refill.note.isNotEmpty()
        tryCalcConsumption()
        et_last_distance.setText(
                if(refill.lastDistance.isEmpty()) ""
                else refill.lastDistance.toString()
        )
    }

    private fun Int.isEmpty() = this == Refill.UNSET_INT

    private fun getRadioButtonId(trafficMode: Refill.TrafficMode) =
            when (trafficMode) {
                Refill.TrafficMode.CITY -> R.id.rb_city_mode
                Refill.TrafficMode.HIGHWAY -> R.id.rb_highway_mode
                Refill.TrafficMode.MIXED -> R.id.rb_mixed_mode
            }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.menu_refill_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> onDeleteClicked()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    private fun onDeleteClicked() {
        viewModel.delete(argRefillId)
    }

    companion object {
        private const val ARG_REFILL_ID = "ARG_REFILL_ID"

        fun newInstance(fuelType: FuelType, refillId: Long): BaseFragment {
            val args = getArguments(fuelType)
            args.putLong(ARG_REFILL_ID, refillId)

            return EditRefillDetailsFragment().apply { arguments = args }
        }
    }
}