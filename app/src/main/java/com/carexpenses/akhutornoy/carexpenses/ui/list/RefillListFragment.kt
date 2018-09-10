package com.carexpenses.akhutornoy.carexpenses.ui.list

import android.arch.lifecycle.Observer
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.carexpenses.akhutornoy.carexpenses.R
import com.carexpenses.akhutornoy.carexpenses.base.BaseDaggerFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import com.carexpenses.akhutornoy.carexpenses.base.BaseViewModel
import com.carexpenses.akhutornoy.carexpenses.domain.Refill
import com.carexpenses.akhutornoy.carexpenses.ui.list.recyclerview.RefillListAdapter
import com.carexpenses.akhutornoy.carexpenses.ui.list.recyclerview.RefillItem
import kotlinx.android.synthetic.main.fragment_refill_list.*
import javax.inject.Inject

class RefillListFragment : BaseDaggerFragment() {
    @Inject
    lateinit var viewModel : RefillListViewModel

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
        return R.layout.fragment_refill_list
    }

    override fun getBaseViewModel(): BaseViewModel? {
        return viewModel
    }

    override fun getProgressBar(): View? = progress_bar

    override fun init() {
        initListeners()
        loadFromDb()
    }

    private fun initListeners() {
        add_fab.setOnClickListener { showAddNewItemScreen() }
    }

    //TODO maybe should be replaced with Navigation pack library
    private fun showAddNewItemScreen() {
        navigationCallback.navigateToCreateNewRefill()
    }

    private fun loadFromDb() {
        viewModel.getRefills(FUEL_TYPE).observe(this,
                Observer { items -> showList(items!!) })
    }

    private fun showList(refills: List<RefillItem>) {
        //TODO investigate: why the method is called many times on LpgFragmnet.Done button clicked. Maybe because of observable.
        val adapter = RefillListAdapter(
                refills,
                listener = object : RefillListAdapter.OnItemSelected<RefillItem> {
                    override fun onItemSelected(item: RefillItem) {
                        navigationCallback.navigateToEditRefill(item.dbId)
                    }
                }
        )
        recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            this.adapter = adapter
        }
    }

    companion object {
        private val FUEL_TYPE = Refill.FuelType.LPG

        fun newInstance(): BaseFragment {
            return RefillListFragment()
        }
    }

    interface Navigation {
        fun navigateToCreateNewRefill()
        fun navigateToEditRefill(refillId: Long)
    }

}