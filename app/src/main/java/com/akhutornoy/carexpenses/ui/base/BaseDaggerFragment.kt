package com.akhutornoy.carexpenses.ui.base

import android.os.Bundle
import com.akhutornoy.carexpenses.ui.utils.getAttachedViewModels
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseDaggerFragment : BaseFragment(), HasSupportFragmentInjector {

    @Inject
    protected lateinit var childFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

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

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> {
        return childFragmentInjector
    }
}