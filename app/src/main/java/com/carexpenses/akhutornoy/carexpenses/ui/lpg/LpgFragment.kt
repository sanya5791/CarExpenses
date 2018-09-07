package com.carexpenses.akhutornoy.carexpenses.ui.lpg

import android.arch.lifecycle.Observer
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.carexpenses.akhutornoy.carexpenses.base.exceptions.ItemNotFoundExeption
import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseDaggerFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.Refill.DistanceMode
import kotlinx.android.synthetic.main.fragment_lpg.*
import javax.inject.Inject

private const val TEMP_REFILL_ID = 1L

class LpgFragment : BaseDaggerFragment() {
    @Inject
    lateinit var viewModel : RefillViewModel

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_lpg
    }

    override fun getBaseViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun init() {
        initListeners()
        loadFromDb()
    }

    private fun initListeners() {
        bt_done.setOnClickListener { onButtonDoneClicked() }
    }

    private fun onButtonDoneClicked() {
        val refill = Refill(
                createdAt = TEMP_REFILL_ID,
                litersCount = et_liters.getIntValue(),
                moneyCount = et_money.getIntValue(),
                lastDistance = et_last_distance.getIntValue(),
                distanceMode = getSelectedDistanceMode().value,
                note = et_note.text.toString()
        )
        viewModel.insert(refill).observe(this, Observer {isInserted ->
            //TODO get rid of check Null
            if (isInserted == null) {
                return@Observer
            }
            if(isInserted){
                onInsertedSuccess()
            }
        })
    }

    private fun onInsertedSuccess() {
        Toast.makeText(requireActivity().applicationContext, "Saved", Toast.LENGTH_SHORT).show()
    }

    private fun EditText.getIntValue() =
            this.text.toString().toIntOrNull() ?: Refill.UNSET_INT

    private fun getSelectedDistanceMode() =
            when (rg_distance_mode.checkedRadioButtonId) {
                R.id.rb_city_mode -> DistanceMode.CITY
                R.id.rb_highway_mode -> DistanceMode.HIGHWAY
                R.id.rb_mixed_mode -> DistanceMode.MIXED
                else -> throw IllegalArgumentException("Not a case")
            }

    private fun loadFromDb() {
        viewModel.getById(TEMP_REFILL_ID)
                .observe(this, Observer { showRefill(it!!) })
    }

    private fun handleError(error: Throwable) {
        if (error is ItemNotFoundExeption) {
            showInfoMessage("No Last Refill item")
        }
        onError(error)
    }

    private fun showRestoredFromDb(it: List<Refill>) {
        val firstOrNull = it.firstOrNull()
        if(firstOrNull == null) {
            showInfoMessage("No Last Refill item")
        } else {
            showRefill(firstOrNull)
        }
    }

    private fun showRefill(refill: Refill) {
        et_liters.setText(refill.litersCount.toString())
        et_money.setText(refill.moneyCount.toString())
        et_last_distance.setText(refill.lastDistance.toString())
        //todo calc consumption
        rg_distance_mode.check(getRadioButtonId(refill.distanceMode()))
        et_note.setText(refill.note)
    }

    private fun getRadioButtonId(distanceMode: DistanceMode) =
        when (distanceMode) {
            DistanceMode.CITY -> R.id.rb_city_mode
            DistanceMode.HIGHWAY -> R.id.rb_highway_mode
            DistanceMode.MIXED -> R.id.rb_mixed_mode
        }

    companion object {
        fun newInstance(): LpgFragment {
            return LpgFragment()
        }
    }
}