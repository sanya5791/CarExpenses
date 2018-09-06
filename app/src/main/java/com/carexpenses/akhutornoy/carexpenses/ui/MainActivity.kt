package com.carexpenses.akhutornoy.carexpenses.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.carexpenses.akhutornoy.carexpenses.ui.lpg.LpgFragment
import com.carexpenses.akhutornoy.carexpenses.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initListeners()
    }

    private fun initListeners() {
        navigation.setOnNavigationItemSelectedListener({item ->
            when (item.itemId) {
                R.id.navigation_lpg -> {
                    onLpgClicked()
                    true
                }
                R.id.navigation_dashboard -> {
                    onDashboardClicked()
                    true
                }
                R.id.navigation_notifications -> {
                    message.setText(R.string.title_notifications)
                    true
                }
                else -> false
            }
        })
    }

    private fun onLpgClicked() {
        message.setText(R.string.title_lpg)
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LpgFragment.newInstance())
                .commit()
    }

    private fun onDashboardClicked() {
        message.setText(R.string.title_dashboard)
    }
}
