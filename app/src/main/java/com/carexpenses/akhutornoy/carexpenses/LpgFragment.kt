package com.carexpenses.akhutornoy.carexpenses

import android.arch.lifecycle.ViewModelProviders
import android.widget.EditText
import android.widget.Toast
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.Refill.DistanceMode
import com.carexpenses.akhutornoy.carexpenses.utils.applySchedulersCompletable
import com.carexpenses.akhutornoy.carexpenses.utils.applySchedulersSingle
import kotlinx.android.synthetic.main.fragment_lpg.*

private const val TEMP_REFILL_ID = 1L

class LpgFragment : BaseFragment<RefillViewModel>() {

    override val viewModelClass = RefillViewModel::class.java

//    private lateinit var refillDao: RefillDao

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_lpg
    }

    override fun init() {
        ViewModelProviders.of(this, null)
        initListeners()
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

        unsubscribeOnStop(
                viewModel.insert(refill)
                .applySchedulersCompletable()
                .subscribe(
                        { Toast.makeText(requireActivity(), "Saved", Toast.LENGTH_SHORT).show() },
                        { onError(it) })
        )
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

    override fun onStart() {
        super.onStart()
//        refillDao = Db.getInstance(requireActivity()).refillDao()
        loadFromDb()
    }

    private fun loadFromDb() {
        unsubscribeOnStop(
                viewModel
                        .getById(TEMP_REFILL_ID)
                        .applySchedulersSingle()
                        .subscribe(
                                { showRefill(it) },
                                { handleError(it) }
                        )
        )
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