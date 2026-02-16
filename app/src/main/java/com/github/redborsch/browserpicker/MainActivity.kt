package com.github.redborsch.browserpicker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.redborsch.browserpicker.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.main.NavigationHandler
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.binding.setContentView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val setupViewModel = viewModels<SetupViewModel>().value

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding)

        binding.setUp()
        NavigationHandler(this, binding, setupViewModel).setUp(savedInstanceState)

        lifecycle.addObserver(setupViewModel.createRefresher())
    }

    private fun ActivityMainBinding.setUp() {
        setSupportActionBar(toolbar)
    }
}
