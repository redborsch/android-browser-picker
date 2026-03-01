package com.github.redborsch.browserpicker.shared.system.browser

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

@RequiresApi(Build.VERSION_CODES.Q)
internal class RoleManagerStrategy(
    private val fragment: Fragment,
    private val backupStrategy: DefaultBrowserActionStrategy,
) : DefaultBrowserActionStrategy {

    init {
        require(fragment is BrowserRequestSucceededListener)
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(
                source: LifecycleOwner,
                event: Lifecycle.Event
            ) {
                if (event == Lifecycle.Event.ON_CREATE) {
                    RoleManagerStrategyFragment
                        .getOrCreateInstance(fragment)
                        .backupStrategy = backupStrategy
                    source.lifecycle.removeObserver(this)
                }
            }
        })
    }

    override fun launchDefaultBrowserSettings(force: Boolean) {
        RoleManagerStrategyFragment.getOrCreateInstance(fragment).launchDefaultBrowserSettings(force)
    }

    override fun onSettingsLaunchFailed(block: OnSettingsLaunchFailed) {
        // Role Manager should, by idea, be always available, so we just protect the default
        // settings launch.
        backupStrategy.onSettingsLaunchFailed(block)
    }
}
