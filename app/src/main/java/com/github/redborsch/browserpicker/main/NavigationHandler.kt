package com.github.redborsch.browserpicker.main

import android.os.Bundle
import android.view.Menu
import androidx.annotation.IdRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.databinding.ActivityMainBinding
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.replaceCurrentFragment
import com.github.redborsch.fragment.resetCurrentFragment
import com.github.redborsch.lifecycle.launchOnEachStart
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class NavigationHandler(
    private val activity: FragmentActivity,
    private val binding: ActivityMainBinding,
    private val setupViewModel: SetupViewModel,
) {

    private val navigationModel = NavigationModel()
    private val uiPreferences = UiPreferences(activity, "_main_screen")

    fun setUp(savedInstanceState: Bundle?) {
        binding.bottomNavigation.setUp(savedInstanceState)
    }

    fun onStateRestored() {
        navigationModel.findItem(binding.bottomNavigation.selectedItemId)?.applyProperties()
    }

    fun resetCurrentFragment() {
        activity.supportFragmentManager.resetCurrentFragment(
            activity.defaultFragmentTag, binding.fragmentHost
        )
    }

    private fun navigateTo(@IdRes itemId: Int): Boolean {
        val item = navigationModel.findItem(itemId) ?: return false
        activity.supportFragmentManager.replaceCurrentFragment(
            activity.defaultFragmentTag,
            item.fragmentClass,
            binding.fragmentHost,
        )
        item.applyProperties()
        uiPreferences.defaultScreen = item.settingsId
        return true
    }

    private fun NavigationItem.applyProperties() {
        binding.fabTryIt.run {
            if (showTryFab) {
                show()
            } else {
                hide()
            }
        }
    }

    private fun BottomNavigationView.setUp(savedInstanceState: Bundle?) {
        inflateMenu(R.menu.main_navigation)
        setOnItemSelectedListener {
            navigateTo(it.itemId)
        }
        if (savedInstanceState == null) {
            maybeLoadDefaultTab()
            navigateTo(selectedItemId)
        }
        updateSetupTabIcon(menu)
    }

    private fun BottomNavigationView.maybeLoadDefaultTab() {
        val screenSettingsId = uiPreferences.defaultScreen ?: return
        val navigationItem = navigationModel.findItem(screenSettingsId) ?: return
        selectedItemId = navigationItem.menuItemId
    }

    private fun updateSetupTabIcon(menu: Menu) {
        val setupItem = menu.findItem(navigationModel.setupItem.menuItemId) ?: return
        activity.launchOnEachStart {
            setupViewModel.isDefault
                .map {
                    when (it) {
                        null, false -> R.drawable.setup_24
                        true -> R.drawable.check_circle_24
                    }
                }
                .distinctUntilChanged()
                .collect { iconId ->
                    setupItem.icon = AppCompatResources.getDrawable(activity, iconId)
                }
        }
    }
}
