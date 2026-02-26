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
import com.github.redborsch.insets.InsetLocation
import com.github.redborsch.insets.applyDefaults
import com.github.redborsch.insets.applyInsetsAsMargins
import com.github.redborsch.insets.applyInsetsAsPaddings
import com.github.redborsch.insets.enableEdgeToEdge
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity(), SettingsFragment.Host {

    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        DynamicColors.applyToActivityIfAvailable(this)

        val setupViewModel = viewModels<SetupViewModel>().value

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding)
        binding.setUp()

        navigationHandler = NavigationHandler(this, binding, setupViewModel).apply {
            setUp(savedInstanceState)
        }

        lifecycle.addObserver(setupViewModel.createRefresher())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        navigationHandler.onStateRestored()
    }

    override fun onSettingsReset() {
        navigationHandler.resetCurrentFragment()
    }

    private fun ActivityMainBinding.setUp() {
        setSupportActionBar(toolbar)

        nestedScrollView.applyInsetsAsPaddings(InsetLocation { LEFT + RIGHT })
        toolbar.applyInsetsAsMargins(InsetLocation { LEFT + RIGHT + TOP })
        fabTryIt.applyInsetsAsMargins(InsetLocation.RIGHT, keepExisting = true)

        fabTryIt.setOnClickListener {
            tryChooser()
        }

        protectionLayout.applyDefaults()
    }
}
