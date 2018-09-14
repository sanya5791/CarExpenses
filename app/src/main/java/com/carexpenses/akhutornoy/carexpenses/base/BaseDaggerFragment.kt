package com.carexpenses.akhutornoy.carexpenses.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.carexpenses.akhutornoy.carexpenses.utils.getAttachedViewModels
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseDaggerFragment : BaseFragment(), HasSupportFragmentInjector {

    @Inject
    protected lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)

        savedInstanceState?.apply {
            getAttachedViewModels().filter { it.value is BaseSavableViewModel }
                    .forEach { (it.value as BaseSavableViewModel).restore(savedInstanceState) }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        getAttachedViewModels().filter { it.value is BaseSavableViewModel }
                .forEach { (it.value as BaseSavableViewModel).save(outState) }

        super.onSaveInstanceState(outState)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return childFragmentInjector
    }
}