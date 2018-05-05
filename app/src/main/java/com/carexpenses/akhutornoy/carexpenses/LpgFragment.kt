package com.carexpenses.akhutornoy.carexpenses

import com.carexpenses.akhutornoy.carexpenses.base.BaseFragment

class LpgFragment : BaseFragment() {

    companion object {
        fun newInstance(): LpgFragment {
            return LpgFragment()
        }
    }

    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_lpg
    }
}