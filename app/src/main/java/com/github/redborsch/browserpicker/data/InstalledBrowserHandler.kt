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
import com.github.redborsch.browserpicker.common.closeChooser
import com.github.redborsch.browserpicker.shared.model.BrowserData
import com.github.redborsch.browserpicker.shared.model.getNameWithTimeout
import com.github.redborsch.log.dumpForLog
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

        log.d { "Intent to launch browser: ${intent.dumpForLog()}" }

        fragment.launchAndClose(intent, browserData)
    }

    private fun Fragment.launchAndClose(intent: Intent, browserData: BrowserData) {
        val activity = activity ?: return

        if (!Settings.getInstance(activity).keepInRecents) {
            // This task will be removed, so we need to make sure we start a new one
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (!activity.isInMultiWindowMode) {
            activity.disablePendingTransitions()
        }

        activity.launch(intent, browserData)
        activity.closeChooser()
    }

    private fun Activity.launch(intent: Intent, browserData: BrowserData) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            log.e(e) { "Error opening $intent, browser $browserData" }
            Toast.makeText(
                this,
                getString(R.string.open_error, browserData.getNameWithTimeout(this)),
                Toast.LENGTH_LONG
            ).show()
            return
        }
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
}
