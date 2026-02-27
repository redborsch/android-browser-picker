package com.github.redborsch.browserpicker.data

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.chooser.BrowserIntentFactory
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.log.getLogger

class InstalledBrowserHandler : BrowserDataHandler {

    private val log = getLogger()

    override val browserData: BrowserData?
        get() = null

    override fun handles(browserData: BrowserData): Boolean {
        // This handler works as a fallback, so needs to be added as the last one.
        return true
    }

    override fun handle(
        browserData: BrowserData,
        intentFactory: BrowserIntentFactory,
        fragment: Fragment
    ) {
        val intent = intentFactory.createBrowserIntent(browserData.packageName)
        fragment.launchAndClose(intent, browserData)
    }

    private fun Fragment.launchAndClose(intent: Intent, browserData: BrowserData) {
        val activity = activity ?: return
        activity.disablePendingTransitions()
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            log.e(e) { "Error opening $intent, browser $browserData" }
            Toast.makeText(
                activity,
                getString(R.string.open_error, browserData.getName(activity)),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        activity.close()
    }

    private fun Activity.disablePendingTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, 0, 0)
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun Activity.close() {
        if (Settings.getInstance(this).keepInRecents) {
            finish()
        } else {
            finishAndRemoveTask()
        }
    }
}
