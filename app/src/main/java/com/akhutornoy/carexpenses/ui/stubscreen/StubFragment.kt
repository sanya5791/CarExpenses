package com.akhutornoy.carexpenses.ui.stubscreen

import android.view.View
import com.akhutornoy.carexpenses.R
import com.akhutornoy.carexpenses.base.BaseFragment
import com.akhutornoy.carexpenses.base.BaseViewModel
import kotlinx.android.synthetic.main.progress_bar_view.*

class StubFragment : BaseFragment() {
    override fun fragmentLayoutId(): Int {
        return R.layout.fragment_stub
    }

    override fun init() { }

    override fun getBaseViewModel(): BaseViewModel? = null

    override fun getProgressBar(): View? = progress_bar

    companion object {
        fun newInstance(): BaseFragment {
            return StubFragment()
        }
    }
}