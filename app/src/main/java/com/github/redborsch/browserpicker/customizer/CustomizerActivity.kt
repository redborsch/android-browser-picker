package com.github.redborsch.browserpicker.customizer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.redborsch.binding.setContentView
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.tryChooser
import com.github.redborsch.browserpicker.databinding.ActivityCustomizerBinding
import com.github.redborsch.insets.InsetLocation
import com.github.redborsch.insets.applyDefaults
import com.github.redborsch.insets.applyInsetsAsMargins
import com.github.redborsch.insets.applyInsetsAsPaddings
import com.github.redborsch.insets.enableEdgeToEdge
import com.google.android.material.color.DynamicColors

class CustomizerActivity : AppCompatActivity() {

    private val viewModel: CustomizerViewModel by viewModels()

    private val listHelper by lazy {
        CustomizerListHelper(viewModel, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        DynamicColors.applyToActivityIfAvailable(this)

        val binding = ActivityCustomizerBinding.inflate(layoutInflater)
        setContentView(binding)
        binding.setUp()

        listHelper.setUp(binding.recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_customizer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_confirm -> confirmCustomizations()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun ActivityCustomizerBinding.setUp() {
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        fabTryIt.setOnClickListener {
            tryChooser(customSettings = listHelper.collectSettings())
        }

        fabTryIt.applyInsetsAsMargins(
            InsetLocation { LEFT + RIGHT + BOTTOM },
            keepExisting = true,
        )
        recyclerView.applyInsetsAsPaddings(InsetLocation { LEFT + RIGHT + BOTTOM })
        toolbar.applyInsetsAsMargins(InsetLocation { LEFT + RIGHT + TOP })

        protectionLayout.applyDefaults()
    }

    private fun confirmCustomizations() {
        viewModel.saveSettings(listHelper.collectSettings())
        finish()
    }
}
