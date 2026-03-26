package com.github.redborsch.browserpicker.chooser

import android.app.assist.AssistContent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.ContentView
import androidx.appcompat.app.AppCompatActivity
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.makeCopyWithoutComponent
import com.github.redborsch.browserpicker.common.toSystemChooser
import com.github.redborsch.browserpicker.shared.repository.BrowserListSettings
import com.github.redborsch.insets.enableEdgeToEdge
import com.github.redborsch.log.dumpForLog
import com.github.redborsch.log.getLogger
import com.github.redborsch.os.tryGetParcelableExtra

abstract class AbstractChooserActivity : AppCompatActivity {

    protected val log = getLogger()

    private val viewModel: BrowserChooserViewModel by viewModels()

    private val Intent.originalIntent get() = tryGetParcelableExtra<Intent>(KEY_ORIGINAL_INTENT)

    constructor() : super()
    @ContentView
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        log.d { "onCreate $savedInstanceState" }

        processIntent(intent)
    }

    final override fun onProvideAssistContent(outContent: AssistContent) {
        super.onProvideAssistContent(outContent)

        outContent.webUri = intent?.originalIntent?.data
    }

    private fun processIntent(newIntent: Intent?) {
        var intent = newIntent?.originalIntent ?: return

        log.d { "Activity intent: ${intent.dumpForLog()}" }

        intent = maybeConsumeCustomSettings(intent)

        val browserIntentFactory = BrowserIntentFactory(
            intent,
            viewModel.settings.useOriginalIntent,
        ) ?: run {
            maybeReshare(intent)
            return
        }
        viewModel.setBrowserIntentFactory(browserIntentFactory)
    }

    private fun maybeReshare(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return

        val context = this@AbstractChooserActivity
        Toast.makeText(
            context,
            R.string.toast_send_intent_warning,
            Toast.LENGTH_LONG,
        ).show()

        val newShareIntent = intent
            .makeCopyWithoutComponent()
            .toSystemChooser(context)
        startActivity(newShareIntent)
        finish()
    }

    private fun maybeConsumeCustomSettings(intent: Intent): Intent =
        if (intent.hasExtra(EXTRA_CUSTOM_SETTINGS)) {
            log.v { "Consuming custom settings" }

            viewModel.customBrowserListSettings = BrowserListSettings.deserialize(
                intent.getStringArrayExtra(EXTRA_CUSTOM_SETTINGS)
                    ?.toSet() ?: emptySet()
            )

            Intent(intent).apply {
                removeExtra(EXTRA_CUSTOM_SETTINGS)
            }
        } else {
            log.v { "No custom settings" }

            viewModel.customBrowserListSettings = null
            intent
        }

    companion object {
        private val PREFIX get() = AbstractChooserActivity::class.qualifiedName

        private val KEY_ORIGINAL_INTENT get() = "$PREFIX.OriginalIntent"

        private val EXTRA_CUSTOM_SETTINGS get() = "$PREFIX.CustomSettings"

        fun isChooserIntent(intent: Intent): Boolean =
            intent.hasExtra(KEY_ORIGINAL_INTENT)

        fun putCustomSettings(
            intent: Intent,
            customSettings: BrowserListSettings,
        ) {
            intent.putExtra(EXTRA_CUSTOM_SETTINGS, customSettings.serialize().toTypedArray())
        }

        fun <T : AbstractChooserActivity> createIntent(context: Context, activityClass: Class<T>, originalIntent: Intent) = Intent(context, activityClass).apply {
            putExtra(KEY_ORIGINAL_INTENT, originalIntent)
        }
    }
}
