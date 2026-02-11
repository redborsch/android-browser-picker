package com.github.redborsch.browserpicker.playground

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.redborsch.browserpicker.playground.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.shared.fragment.setContentView

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding)

        UiHelper(viewModel).setUp(binding, lifecycleScope)
    }
}
