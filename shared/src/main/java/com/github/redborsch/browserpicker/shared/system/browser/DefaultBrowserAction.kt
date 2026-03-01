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

/**
 * Used by [RoleManagerStrategy] to inform that the request has succeeded.
 * The fragment using [DefaultBrowserAction] must implement it (enforced in runtime).
 */
interface BrowserRequestSucceededListener {
    fun onBrowserRequestSucceeded()
}
typealias OnSettingsLaunchFailed = (e: ActivityNotFoundException) -> Unit

interface DefaultBrowserActionStrategy {
    fun launchDefaultBrowserSettings(force: Boolean)
    fun onSettingsLaunchFailed(block: OnSettingsLaunchFailed)
}
