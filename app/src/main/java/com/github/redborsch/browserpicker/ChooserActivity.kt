package com.github.redborsch.browserpicker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.github.redborsch.browserpicker.chooser.BrowserChooserBottomSheetFragment
import com.github.redborsch.browserpicker.chooser.BrowserChooserViewModel
import com.github.redborsch.browserpicker.chooser.BrowserIntentFactory
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.common.toSystemChooser
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog
import com.github.redborsch.insets.enableEdgeToEdge
import com.github.redborsch.log.dumpForLog
import com.github.redborsch.log.getLogger

class ChooserActivity : FragmentActivity() {

    private val log = getLogger()

    private val viewModel: BrowserChooserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        processIntent(intent)
    }

    private fun processIntent(newIntent: Intent?) {
        var intent = newIntent ?: return

        log.d { "Activity intent: ${intent.dumpForLog()}" }

        intent = maybeConsumeCustomSettings(intent)

        val browserIntentFactory = BrowserIntentFactory(
            intent,
            viewModel.settings.useOriginalIntent,
        ) ?: run {
            maybeReshare(intent)
            return
        }

        supportFragmentManager.showDialog(defaultFragmentTag, forceReplace = true) {
            BrowserChooserBottomSheetFragment.newInstance(browserIntentFactory)
        }
    }

    private fun maybeConsumeCustomSettings(intent: Intent): Intent {
        return if (intent.hasExtra(EXTRA_CUSTOM_SETTINGS)) {
            viewModel.browserListSettings = BrowserListSettings.deserialize(
                intent.getStringArrayExtra(EXTRA_CUSTOM_SETTINGS)
                    ?.toSet() ?: emptySet()
            )

            Intent(intent).apply {
                removeExtra(EXTRA_CUSTOM_SETTINGS)
            }
        } else {
            intent
        }
    }

    private fun maybeReshare(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return

        Toast.makeText(this, R.string.toast_send_intent_warning, Toast.LENGTH_LONG).show()

        val newShareIntent = Intent(intent).apply {
            setComponent(null)
        }.toSystemChooser(this)
        startActivity(newShareIntent)
        finish()
    }

    companion object {

        private val EXTRA_CUSTOM_SETTINGS get() = ChooserActivity::class.qualifiedName + ".CustomSettings"

        fun createIntent(
            context: Context,
            url: String = Settings.getInstance(context).testUrl,
            customSettings: BrowserListSettings? = null,
        ) = Intent(context, ChooserActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = url.toUri()
            if (customSettings != null) {
                putExtra(EXTRA_CUSTOM_SETTINGS, customSettings.serialize().toTypedArray())
            }
        }
    }
}
