package com.carexpenses.akhutornoy.carexpenses

import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_lpg.*

class LpgFragment : BaseFragment() {
    companion object {

        fun newInstance(): LpgFragment {
            return LpgFragment()
        }
    }

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_lpg
    }

    override fun initViews() {
        initListeners()
    }

    private fun initListeners() {

    }
}