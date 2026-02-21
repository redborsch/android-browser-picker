package com.github.redborsch.browserpicker.main

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.settings.SettingsFragment
import kotlin.reflect.KClass

class NavigationItem(
    val fragmentClass: KClass<out Fragment>,
    @field:IdRes val menuItemId: Int,
    val settingsId: String,
)

class NavigationModel {

    val setupItem = NavigationItem<TryFragment>(R.id.menu_setup, "setup")

    private val items: List<NavigationItem> = buildList(2) {
        add(setupItem)
        add(NavigationItem<SettingsFragment>(R.id.menu_settings, "settings"))
    }

    fun findItem(@IdRes itemId: Int): NavigationItem? = items.find {
        it.menuItemId == itemId
    }

    fun findItem(settingsId: String): NavigationItem? = items.find {
        it.settingsId == settingsId
    }
}

private inline fun <reified F : Fragment> NavigationItem(
    @IdRes menuItemId: Int,
    settingsId: String,
) = NavigationItem(F::class, menuItemId, settingsId)
