package com.github.redborsch.browserpicker.shared.system.browser

import android.content.ActivityNotFoundException
import android.os.Build
import androidx.fragment.app.Fragment

class DefaultBrowserAction(
    strategy: DefaultBrowserActionStrategy,
) : DefaultBrowserActionStrategy by strategy

fun DefaultBrowserAction(fragment: Fragment): DefaultBrowserAction {
    var strategy: DefaultBrowserActionStrategy = DefaultStrategy(fragment)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        strategy = RoleManagerStrategy(fragment, strategy)
    }
    return DefaultBrowserAction(strategy)
}

typealias OnBrowserRequestSucceeded = () -> Unit
typealias OnSettingsLaunchFailed = (e: ActivityNotFoundException) -> Unit

interface DefaultBrowserActionStrategy {
    fun launchDefaultBrowserSettings()
    fun onBrowserRequestSucceeded(block: OnBrowserRequestSucceeded)
    fun onSettingsLaunchFailed(block: OnSettingsLaunchFailed)
}
