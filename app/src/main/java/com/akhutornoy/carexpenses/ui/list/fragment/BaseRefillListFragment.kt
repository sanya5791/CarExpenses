package com.akhutornoy.carexpenses.ui.list.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.ui.base.*
import com.akhutornoy.carexpenses.ui.list.model.FilterDateRange
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.model.RefillItem
import com.akhutornoy.carexpenses.ui.list.model.RefillResult
import com.akhutornoy.carexpenses.ui.list.recyclerview.RefillListAdapter
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import com.akhutornoy.carexpenses.ui.utils.DATE_FORMAT
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_summary.*
import kotlinx.android.synthetic.main.fragment_refill_list.*
import kotlinx.android.synthetic.main.summary.*
import kotlinx.android.synthetic.main.toolbar.*
import org.joda.time.LocalDate
import java.util.*


abstract class BaseRefillListFragment<T> : BaseDaggerFragment() {

    abstract val viewModel: BaseRefillListViewModel<T>

    private val fuelType: FuelType by lazy { FuelType.valueOf(arguments?.getString(FUEL_TYPE_ARG)!!) }

    private lateinit var navigationCallback: Navigation
    protected lateinit var toolbar: IToolbar

    protected open val addFabVisibility = VISIBLE
    protected open val fuelTypeVisibility = GONE

    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(view!!.findViewById(R.id.summary_bottom_sheet)!!) }
    protected open val isSummaryHeightExtended: Boolean = true

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Navigation) {
            navigationCallback = context
        } else {
            IllegalArgumentException("Calling Activity='${context!!::class.java.simpleName}' should implement '${Navigation::class.java.simpleName}' interface")
        }
    }

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_refill_list
    }

    override fun getBaseViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun getProgressBar(): View? = progress_bar

    override fun loadData() {
        loadRefills()
    }

    override fun initViewModelObservers() {
        viewModel.onLoadRefillsLiveData.observe(this,
                Observer { refillResult -> showResult(refillResult!!) })
    }

    @SuppressLint("RestrictedApi")
    override fun initView() {
        initToolbar()
        initBottomSheet()

        add_fab.visibility = addFabVisibility
        add_fab.setOnClickListener { showAddNewItemScreen() }

        fuel_type_text_vew.visibility = fuelTypeVisibility

        filter_clear_image_view.setOnClickListener { onClearFilterClicked() }
    }

    protected open fun initToolbar() {
        toolbar = BaseToolbar(activity as BaseActivity)
        setHasOptionsMenu(true)
        toolbar.setToolbar(toolbar_view, false)
        toolbar.setToolbarTitle(R.string.refill_list_refills_title)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.menu_refill_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_filter -> onFilterClicked()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onFilterClicked(): Boolean {
        DatePickerBuilder(activity, onSelectedDateListener())
                .pickerType(CalendarView.RANGE_PICKER)
                .date(Calendar.getInstance())
                .build()
                .show()
        return true
    }

    private fun onSelectedDateListener() = OnSelectDateListener { calendars ->
        if (calendars.size > 1) {
            val dateFrom = LocalDate(calendars[0].time)
            val dateTo = LocalDate(calendars[calendars.size - 1].time)
            val filterDateRange = FilterDateRange(dateFrom, dateTo)
            showFilterView(filterDateRange)

            loadRefills(filterDateRange)
        }
    }

    private fun showFilterView(filterDateRange: FilterDateRange) {
        filter_view_group.visibility = VISIBLE
        val sFrom = filterDateRange.from.toString(DATE_FORMAT)
        val sTo = filterDateRange.to.toString(DATE_FORMAT)
        val range = "$sFrom - $sTo"
        filter_from_text_view.text = range
    }

    private fun hideFilterView() {
        filter_from_text_view.text = ""
        filter_view_group.visibility = GONE
    }

    private fun initBottomSheet() {
        setBottomSheetHeight()
        bottomSheetBehavior.setBottomSheetCallback(ImageViewRotater(extend_button_image_view))

        val visibility = if (isSummaryHeightExtended) VISIBLE else GONE
        extend_button_image_view.visibility = visibility
        extend_button_image_view.setOnClickListener { switchBottomSheetState() }
    }

    private fun setBottomSheetHeight() {
        @DimenRes val height = if (isSummaryHeightExtended) R.dimen.summary_full_height_bottom_sheet
        else R.dimen.summary_peek_height_bottom_sheet

        val params = summary_bottom_sheet.layoutParams
        params.height = resources.getDimension(height).toInt()
        summary_bottom_sheet.layoutParams = params
    }

    private fun switchBottomSheetState() {
        val newState = when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
            BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
            else -> BottomSheetBehavior.STATE_COLLAPSED
        }
        bottomSheetBehavior.state = newState
    }

    protected fun setSummaryMessage(message: String) {
        summary_message_text_view.text = message
    }

    protected fun setSummaryExtendedMessage(message: String) {
        summary_extended_message_text_view.text = message
    }

    private fun dpsToPixels(activity: Activity, dps: Int): Int {
        val r = activity.resources
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps.toFloat(), r.displayMetrics).toInt()
    }

    private fun showAddNewItemScreen() {
        navigationCallback.navigateToCreateNewRefill(fuelType)
    }

    private fun onClearFilterClicked() {
        hideFilterView()
        loadRefills(FilterDateRange())
        Toast.makeText(activity, getString(R.string.refill_list_filter_cleared), Toast.LENGTH_LONG).show()
    }

    private fun loadRefills() {
        viewModel.loadRefills(fuelType)
    }

    private fun loadRefills(filterDateRange: FilterDateRange) {
        viewModel.loadRefills(fuelType, filterDateRange)
    }

    private fun showResult(result: RefillResult<T>) {
        showList(result.refills)
        handleFilterView(result.filterRange)
        showSummary(result.summary)
    }

    private fun showList(refills: List<RefillItem>) {
        val listener = object : RefillListAdapter.OnItemSelected<RefillItem> {
            override fun onItemSelected(item: RefillItem) {
                if (fuelType != FuelType.ALL) {
                    navigationCallback.navigateToEditRefill(fuelType, item.dbId)
                }
            }
        }

        val adapter = RefillListAdapter(refills, listener)
        adapter.fuelTypeVisibility = fuelTypeVisibility
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
        }
    }

    private fun handleFilterView(filterRange: FilterDateRange) {
        if (filterRange.isEmpty()) {
            hideFilterView()
        } else {
            showFilterView(filterRange)
        }
    }

    private fun showSummary(summary: T) {
        setSummaryMessage(getSummaryMessage(summary))
        setSummaryExtendedMessage(getSummaryExtendedMessage(summary))
    }

    protected abstract fun getSummaryMessage(summary: T): String

    protected fun getSummaryExtendedMessage(summary: T) = ""

    protected companion object {
        const val FUEL_TYPE_ARG = "FUEL_TYPE_ARG"

        fun <T> newInstance(baseRefillFragment: BaseRefillListFragment<T>, fuelType: FuelType): BaseFragment {
            val args = Bundle()
            args.putString(FUEL_TYPE_ARG, fuelType.name)
            baseRefillFragment.arguments = args
            return baseRefillFragment
        }
    }

    private class ImageViewRotater(val imageView: ImageView) : BottomSheetBehavior.BottomSheetCallback() {
        companion object {
            const val MAX_ROTATION = 180f
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            imageView.rotation = slideOffset * MAX_ROTATION * 3
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {}
    }

    interface Navigation {
        fun navigateToCreateNewRefill(fuelType: FuelType)
        fun navigateToEditRefill(fuelType: FuelType, refillId: Long)
    }
}