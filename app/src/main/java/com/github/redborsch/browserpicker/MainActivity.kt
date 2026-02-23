package com.github.redborsch.browserpicker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.redborsch.binding.setContentView
import com.github.redborsch.browserpicker.common.tryChooser
import com.github.redborsch.browserpicker.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.main.NavigationHandler
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.browserpicker.settings.SettingsFragment

class MainActivity : AppCompatActivity(), SettingsFragment.Host {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val setupViewModel = viewModels<SetupViewModel>().value

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding)
        binding.setUp()

        navigationHandler = NavigationHandler(this, binding, setupViewModel).apply {
            setUp(savedInstanceState)
        }

        lifecycle.addObserver(setupViewModel.createRefresher())
    }

    override fun onSettingsReset() {
        navigationHandler.resetCurrentFragment()
    }

    private fun ActivityMainBinding.setUp() {
        setSupportActionBar(toolbar)

        fabTryIt.setOnClickListener {
            tryChooser()
        }
    }
}
