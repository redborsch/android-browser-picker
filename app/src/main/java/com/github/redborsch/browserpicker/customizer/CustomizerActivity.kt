package com.github.redborsch.browserpicker.customizer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.redborsch.binding.setContentView
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.tryChooser
import com.github.redborsch.browserpicker.databinding.ActivityCustomizerBinding

class CustomizerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCustomizerBinding.inflate(layoutInflater)
        setContentView(binding)
        binding.setUp()

        CustomizerListHelper().setUp(binding.recyclerView)
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
            tryChooser()
        }
    }

    private fun confirmCustomizations() {
        // FIXME
        finish()
    }
}
