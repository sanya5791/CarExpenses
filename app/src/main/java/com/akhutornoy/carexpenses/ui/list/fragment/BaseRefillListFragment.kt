package com.akhutornoy.carexpenses.ui.list.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.*
import com.akhutornoy.carexpenses.ui.list.model.FilterDateRange
import com.akhutornoy.carexpenses.ui.list.model.FuelType
import com.akhutornoy.carexpenses.ui.list.model.RefillItem
import com.akhutornoy.carexpenses.ui.list.model.RefillResult
import com.akhutornoy.carexpenses.ui.list.recyclerview.RefillListAdapter
import com.akhutornoy.carexpenses.ui.list.viewmodel.BaseRefillListViewModel
import com.akhutornoy.carexpenses.utils.DATE_FORMAT
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import kotlinx.android.synthetic.main.fragment_refill_list.*
import kotlinx.android.synthetic.main.toolbar.*
import org.joda.time.LocalDate
import java.util.*


abstract class BaseRefillListFragment<T> : BaseDaggerFragment() {

    abstract val viewModel: BaseRefillListViewModel<T>

    private val fuelType: FuelType by lazy { FuelType.valueOf(arguments?.getString(FUEL_TYPE_ARG)!!) }

    private lateinit var navigationCallback: Navigation
    protected lateinit var toolbar: IToolbar

    protected open val addFabVisibility = View.VISIBLE
    protected open val fuelTypeVisibility = View.GONE

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

    override fun initView() {
        initToolbar()
        add_fab.visibility = addFabVisibility
        fuel_type_text_vew.visibility = fuelTypeVisibility
        initListeners()
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
        filter_view_group.visibility = View.VISIBLE
        val sFrom = filterDateRange.from.toString(DATE_FORMAT)
        val sTo = filterDateRange.to.toString(DATE_FORMAT)
        val range = "$sFrom - $sTo"
        filter_from_text_view.text = range
    }

    private fun hideFilterView() {
        filter_from_text_view.text = ""
        filter_view_group.visibility = View.GONE
    }

    private fun initListeners() {
        add_fab.setOnClickListener { showAddNewItemScreen() }
        filter_clear_image_view.setOnClickListener { onClearFilterClicked() }
    }

    //TODO maybe should be replaced with Navigation pack library
    private fun showAddNewItemScreen() {
        navigationCallback.navigateToCreateNewRefill(fuelType)
    }

    private fun onClearFilterClicked() {
        hideFilterView()
        loadRefills(FilterDateRange())
        Toast.makeText(activity, getString(R.string.refill_list_filter_cleared), Toast.LENGTH_LONG).show()
    }

    private fun loadRefills() {
        viewModel.getRefills(fuelType)
    }

    private fun loadRefills(filterDateRange: FilterDateRange) {
        viewModel.getRefills(fuelType, filterDateRange)
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
        summary_results_text_view.text = getSummaryString(summary)
    }

    protected abstract fun getSummaryString(summary: T): String

    protected companion object {
        const val FUEL_TYPE_ARG = "FUEL_TYPE_ARG"

        fun <T> newInstance(baseRefillFragment: BaseRefillListFragment<T>, fuelType: FuelType): BaseFragment {
            val args = Bundle()
            args.putString(FUEL_TYPE_ARG, fuelType.name)
            baseRefillFragment.arguments = args
            return baseRefillFragment
        }
    }

    interface Navigation {
        fun navigateToCreateNewRefill(fuelType: FuelType)
        fun navigateToEditRefill(fuelType: FuelType, refillId: Long)
    }
}