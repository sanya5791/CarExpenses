package com.carexpenses.akhutornoy.carexpenses.ui.lpg

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseDaggerFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.Refill.TrafficMode
import kotlinx.android.synthetic.main.fragment_lpg.*
import java.util.*
import javax.inject.Inject

//TODO rename to RefillFragment, refill_layout, RefillViewModel etc
class LpgFragment : BaseDaggerFragment() {
    @Inject
    lateinit var viewModel : RefillViewModel

    private lateinit var navigationCallback: Navigation

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is Navigation) {
            navigationCallback = context
        } else {
            IllegalArgumentException("Calling Activity='${context!!::class.java.simpleName}' should implement '${Navigation::class.java.simpleName}' interface")
        }
    }

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_lpg
    }

    override fun getBaseViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun getProgressBar(): View? = progress_bar

    override fun init() {
        initListeners()
        arguments?.getLong(ARG_REFILL_ID)
                ?.let {
                    print(it)
                    loadFromDb(it)
                }
    }

    private fun initListeners() {
        bt_done.setOnClickListener { onButtonDoneClicked() }
    }

    private fun onButtonDoneClicked() {
        val refill = Refill(
                createdAt = Date().time,
                litersCount = et_liters.getIntValue(),
                moneyCount = et_money.getIntValue(),
                lastDistance = et_last_distance.getIntValue(),
                fuelType = Refill.FuelType.LPG.value,
                trafficMode = getSelectedDistanceMode().value,
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
        navigationCallback.navigateOnNewRefillCreated()
        Toast.makeText(requireActivity().applicationContext, "Saved", Toast.LENGTH_SHORT).show()
    }

    private fun EditText.getIntValue() =
            this.text.toString().toIntOrNull() ?: Refill.UNSET_INT

    private fun getSelectedDistanceMode() =
            when (rg_distance_mode.checkedRadioButtonId) {
                R.id.rb_city_mode -> TrafficMode.CITY
                R.id.rb_highway_mode -> TrafficMode.HIGHWAY
                R.id.rb_mixed_mode -> TrafficMode.MIXED
                else -> throw IllegalArgumentException("Not a case")
            }

    private fun loadFromDb(refillId: Long) {
        viewModel.getById(refillId)
                .observe(this, Observer { showRefill(it!!) })
    }

    private fun showRefill(refill: Refill) {
        et_liters.setText(refill.litersCount.toString())
        et_money.setText(refill.moneyCount.toString())
        et_last_distance.setText(refill.lastDistance.toString())
        //todo calc consumption
        rg_distance_mode.check(getRadioButtonId(refill.trafficMode()))
        et_note.setText(refill.note)
    }

    private fun getRadioButtonId(trafficMode: TrafficMode) =
        when (trafficMode) {
            TrafficMode.CITY -> R.id.rb_city_mode
            TrafficMode.HIGHWAY -> R.id.rb_highway_mode
            TrafficMode.MIXED -> R.id.rb_mixed_mode
        }

    companion object {
        private val ARG_REFILL_ID = "ARG_REFILL_ID"

        fun newInstance(): BaseFragment {
            return LpgFragment()
        }

        fun newInstance(refillId: Long): BaseFragment {
            val args = Bundle()
            args.putLong(ARG_REFILL_ID, refillId)
            return newInstance().apply { arguments = args }
        }
    }

    interface Navigation {
        fun navigateOnNewRefillCreated()
    }

}