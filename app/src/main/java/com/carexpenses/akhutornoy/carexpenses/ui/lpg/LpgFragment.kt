package com.carexpenses.akhutornoy.carexpenses.ui.lpg

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseDaggerFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.base.IToolbar
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.domain.Refill.TrafficMode
import com.github.ajalt.timberkt.Timber
import kotlinx.android.synthetic.main.fragment_lpg.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import javax.inject.Inject

//TODO rename to RefillFragment, refill_layout, RefillViewModel etc
class LpgFragment : BaseDaggerFragment() {
    @Inject
    lateinit var viewModel : RefillViewModel

    private lateinit var navigationCallback: Navigation
    private lateinit var toolbar: IToolbar

    private val isEditMode: Boolean by lazy { arguments?.getLong(ARG_REFILL_ID) != null }
    private val argRefillId: Long? by lazy { arguments?.getLong(ARG_REFILL_ID) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is Navigation) {
            navigationCallback = context
        } else {
            IllegalArgumentException("Calling Activity='${context!!::class.java.simpleName}' should implement '${Navigation::class.java.simpleName}' interface")
        }

        if (context is IToolbar) {
            toolbar = context
        } else {
            IllegalArgumentException("Calling Activity='${context!!::class.java.simpleName}' should implement '${IToolbar::class.java.simpleName}' interface")
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
        initToolbar()
        initListeners()
        argRefillId?.let { loadFromDb(it) }
    }

    private fun initToolbar() {
        setHasOptionsMenu(true)
        toolbar.setToolbar(toolbar_view, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (isEditMode) {
            activity?.menuInflater?.inflate(R.menu.menu_list, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> onDeleteClicked()
            android.R.id.home -> onBackClicked()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteClicked(): Boolean {
        Timber.d {"Delete clicked"}
        if (argRefillId == null) {
            return false
        }
        viewModel.delete(argRefillId!!).observe(this, Observer { isRemoved ->
            when (isRemoved) {
                true -> navigationCallback.navigationFinishScreen()
            }
        })
        return true
    }

    private fun onBackClicked(): Boolean {
        navigationCallback.navigationFinishScreen()
        return true
    }

    private fun initListeners() {
        bt_done.setOnClickListener { onButtonDoneClicked() }
    }

    private fun onButtonDoneClicked() {
        val refill = getRefillItem()
        viewModel.insert(refill).observe(this, Observer {isInserted ->
            when (isInserted) {
                true -> onInsertedSuccess()
            }
//            isInserted?.takeIf { it }.apply { onInsertedSuccess() } alternate option of solution with 'when'
        })
    }

    private fun getRefillItem(): Refill {
        val timeNow = Date().time
        val createdAt: Long = argRefillId ?: timeNow
        val editedAt: Long =
                if (isEditMode) timeNow
                else createdAt

        return Refill(
                createdAt = createdAt,
                editedAt = editedAt,
                litersCount = et_liters.getIntValue(),
                moneyCount = et_money.getIntValue(),
                lastDistance = et_last_distance.getIntValue(),
                fuelType = Refill.FuelType.LPG.value,
                trafficMode = getSelectedDistanceMode().value,
                note = et_note.text.toString()
        )
    }

    private fun onInsertedSuccess() {
        navigationCallback.navigationFinishScreen()
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
        private const val ARG_REFILL_ID = "ARG_REFILL_ID"

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
        fun navigationFinishScreen()
    }

}