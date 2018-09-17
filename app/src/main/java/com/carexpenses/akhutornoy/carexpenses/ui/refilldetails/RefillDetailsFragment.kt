package com.carexpenses.akhutornoy.carexpenses.ui.refilldetails

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.carexpenses.akhutornoy.carexpenses.ui.list.FuelType
import kotlinx.android.synthetic.main.fragment_refill_details.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*
import javax.inject.Inject

class RefillDetailsFragment : BaseDaggerFragment() {
    @Inject
    lateinit var viewModel : RefillDetailsViewModel

    private lateinit var navigationCallback: Navigation
    private lateinit var toolbar: IToolbar

    private val argFuelType: FuelType by lazy { FuelType.valueOf(arguments?.getString(ARG_FUEL_TYPE)!!) }
    private val argRefillId: Long? by lazy { getRefillIdFromArg() }
    private val isEditMode: Boolean by lazy { arguments!!.containsKey(ARG_REFILL_ID) }

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
            IllegalArgumentException("Calling Activity='${context::class.java.simpleName}' should implement '${IToolbar::class.java.simpleName}' interface")
        }
    }

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_refill_details
    }

    override fun getBaseViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun getProgressBar(): View? = progress_bar

    private fun getRefillIdFromArg(): Long? {
        if (arguments == null || !arguments!!.containsKey(ARG_REFILL_ID)) {
            return null
        }

        return arguments!!.getLong(ARG_REFILL_ID)
    }

    override fun init() {
        initToolbar()
        initListeners()
        argRefillId?.let { loadFromDb(it) }
        viewModel.onConsumptionCalculated.observe(this,
                Observer(this@RefillDetailsFragment::onConsumptionCalculated))
    }

    private fun onConsumptionCalculated(consumption: RefillDetailsViewModel.Consumption?) {
        if (consumption?.isCalculated!!) {
            val str = "%.2f".format(consumption.consumption)
            et_fuel_consumption.setText(str)
        } else {
            et_fuel_consumption.setText("")
        }
    }

    private fun initToolbar() {
        setHasOptionsMenu(true)
        toolbar.setToolbar(toolbar_view, true)
        if (isEditMode) {
            toolbar.setToolbarTitle(R.string.refill_details_title)
        } else {
            toolbar.setToolbarTitle(R.string.refill_details_title_new_entry)
        }

        when (argFuelType) {
            FuelType.LPG -> toolbar.setToolbarSubtitle(R.string.title_lpg)
            FuelType.PETROL -> toolbar.setToolbarSubtitle(R.string.title_petrol)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (isEditMode) {
            activity?.menuInflater?.inflate(R.menu.menu_refill_details, menu)
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
        use_note_check_box.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) til_note.visibility = View.VISIBLE
            else til_note.visibility = View.GONE
        }
    }

    private val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tryCalcConsumption()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun tryCalcConsumption() {
        viewModel.onConsumptionRelatedDataChanged(getRefillItem())
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
                currentMileage = et_current_mileage.getIntValue(),
                lastDistance = et_last_distance.getIntValue(),
                fuelType = FuelType.mapToDbFuelType(argFuelType).value,
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

    private fun Int.isEmpty() = this == Refill.UNSET_INT

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

    private fun getRadioButtonId(trafficMode: TrafficMode) =
        when (trafficMode) {
            TrafficMode.CITY -> R.id.rb_city_mode
            TrafficMode.HIGHWAY -> R.id.rb_highway_mode
            TrafficMode.MIXED -> R.id.rb_mixed_mode
        }

    override fun onStart() {
        super.onStart()
        et_last_distance.addTextChangedListener(textWatcher)
        et_liters.addTextChangedListener(textWatcher)
        et_current_mileage.addTextChangedListener(textWatcher)
    }

    override fun onStop() {
        super.onStop()
        et_last_distance.removeTextChangedListener(textWatcher)
        et_liters.removeTextChangedListener(textWatcher)
        et_current_mileage.removeTextChangedListener(textWatcher)
    }

    companion object {
        private const val ARG_FUEL_TYPE = "ARG_FUEL_TYPE"
        private const val ARG_REFILL_ID = "ARG_REFILL_ID"

        fun newInstance(fuelType: FuelType): BaseFragment {
            return newInstance(fuelType, null)
        }

        fun newInstance(fuelType: FuelType, refillId: Long?): BaseFragment {
            val args = Bundle()
            args.putString(ARG_FUEL_TYPE, fuelType.name)

            refillId?.apply { args.putLong(ARG_REFILL_ID, this) }

            return RefillDetailsFragment().apply { arguments = args }
        }
    }

    interface Navigation {
        fun navigationFinishScreen()
    }
}