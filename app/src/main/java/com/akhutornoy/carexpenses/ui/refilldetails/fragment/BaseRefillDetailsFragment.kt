package com.akhutornoy.carexpenses.ui.refilldetails.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.domain.Refill
import com.akhutornoy.carexpenses.domain.Refill.TrafficMode
import com.akhutornoy.carexpenses.ui.base.*
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.refilldetails.viewmodel.CreateRefillDetailsViewModel
import kotlinx.android.synthetic.main.fragment_refill_details.*
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseRefillDetailsFragment : BaseDaggerFragment() {

    protected abstract val createRefillDetailsViewModel: CreateRefillDetailsViewModel

    protected lateinit var navigationCallback: Navigation
    protected lateinit var toolbar: IToolbar

    private val argFuelType: FuelType by lazy { FuelType.valueOf(arguments?.getString(ARG_FUEL_TYPE)!!) }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is Navigation) {
            navigationCallback = context
        } else {
            IllegalArgumentException("Calling Activity='${context!!::class.java.simpleName}' should implement '${Navigation::class.java.simpleName}' interface")
        }
    }

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_refill_details
    }

    override fun getBaseViewModel(): BaseViewModel? {
        return createRefillDetailsViewModel
    }

    override fun getProgressBar(): View? = progress_bar

    override fun initViewModelObservers() {
        createRefillDetailsViewModel.onConsumptionCalculated.observe(this,
                Observer(this@BaseRefillDetailsFragment::onConsumptionCalculated))
        createRefillDetailsViewModel.onInsertedLiveData.observe(this, Observer { isInserted ->
            when (isInserted) {
                true -> onInsertedSuccess()
            }
//            isInserted?.takeIf { it }.apply { onInsertedSuccess() } alternate option of solution with 'when'
        })
    }

    override fun initView() {
        initToolbar()
        initListeners()
        initViewsVisibility()
        markMandatoryFields()
    }

    override fun loadData() {}

    private fun onConsumptionCalculated(consumption: CreateRefillDetailsViewModel.Consumption?) {
        if (consumption?.isCalculated!!) {
            val str = "%.1f".format(consumption.consumption)
            et_fuel_consumption.setText(str)
        } else {
            et_fuel_consumption.setText("")
        }
    }

    private fun onInsertedSuccess() {
        Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show()
        navigationCallback.navigationFinishScreen()
    }

    protected  open fun initToolbar() {
        toolbar = BaseToolbar(activity as BaseActivity)
        setHasOptionsMenu(true)
        toolbar.setToolbar(toolbar_view, true)

        when (argFuelType) {
            FuelType.LPG -> toolbar.setToolbarSubtitle(R.string.title_lpg)
            FuelType.PETROL -> toolbar.setToolbarSubtitle(R.string.title_petrol)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> onBackClicked()
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun onBackClicked(): Boolean {
        navigationCallback.navigationFinishScreen()
        return true
    }

    private fun initListeners() {
        bt_done.setOnClickListener { createRefillDetailsViewModel.insert(getRefillItem()) }
        use_note_check_box.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) til_note.visibility = View.VISIBLE
            else til_note.visibility = View.GONE
        }
    }

    private fun initViewsVisibility() {
        if(argFuelType == FuelType.PETROL)
            til_last_distance.visibility = View.INVISIBLE
    }

    private fun markMandatoryFields() {
        when (argFuelType) {
            FuelType.LPG -> markLpgMandatoryFields()
            FuelType.PETROL -> markPetrolMandatoryFields()
            else -> throw java.lang.IllegalArgumentException("'${argFuelType.name}' NOT supposed to be used here")
        }
    }

    private fun markLpgMandatoryFields() {
        til_current_mileage.hint = getString(R.string.refill_details_current_mileage)
        val textLastDistance = getString(R.string.all_asterisk) + getString(R.string.refill_details_last_distance)
        til_last_distance.hint = textLastDistance

        til_last_distance.isHintEnabled = true
    }

    private fun markPetrolMandatoryFields() {
        val textCurrentMillage = getString(R.string.all_asterisk) + getString(R.string.refill_details_current_mileage)
        et_current_mileage.hint = textCurrentMillage
        et_last_distance.hint = getString(R.string.refill_details_last_distance)
    }

    protected fun tryCalcConsumption() {
        createRefillDetailsViewModel.onConsumptionRelatedDataChanged(getRefillItem())
    }

    private fun getRefillItem()=  Refill(
                createdAt = getRefillItemCreatedAt(),
                editedAt = getRefillItemEditedAt(),
                litersCount = et_liters.getIntValue(),
                moneyCount = et_money.getIntValue(),
                currentMileage = et_current_mileage.getIntValue(),
                lastDistance = et_last_distance.getIntValue(),
                fuelType = FuelType.mapToDbFuelType(argFuelType).value,
                trafficMode = getSelectedDistanceMode().value,
                note = et_note.text.toString()
        )

    abstract fun getRefillItemCreatedAt(): Long

    abstract fun getRefillItemEditedAt(): Long

    private fun EditText.getIntValue() =
            this.text.toString().toIntOrNull() ?: Refill.UNSET_INT

    private fun getSelectedDistanceMode() =
            when (rg_distance_mode.checkedRadioButtonId) {
                R.id.rb_city_mode -> TrafficMode.CITY
                R.id.rb_highway_mode -> TrafficMode.HIGHWAY
                R.id.rb_mixed_mode -> TrafficMode.MIXED
                else -> throw IllegalArgumentException("Not a case")
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

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            tryCalcConsumption()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    companion object {
        private const val ARG_FUEL_TYPE = "ARG_FUEL_TYPE"

        fun getArguments(fuelType: FuelType): Bundle {
            val args = Bundle()
            args.putString(ARG_FUEL_TYPE, fuelType.name)
            return args
        }
    }

    interface Navigation {
        fun navigationFinishScreen()
    }
}