package com.github.redborsch.browserpicker.main

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.preference.PreferenceFragmentCompat
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.createChooserIntent

class SettingsFragment : PreferenceFragmentCompat() {

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(
            menu: Menu,
            menuInflater: MenuInflater
        ) {
            menuInflater.inflate(R.menu.settings, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.menu_test_it -> openChooser()
            }
            return true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().addMenuProvider(menuProvider, this)
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        //TODO("Not yet implemented")
    }

    private fun openChooser() {
        val context = context ?: return
        startActivity(createChooserIntent(context))
    }
}
