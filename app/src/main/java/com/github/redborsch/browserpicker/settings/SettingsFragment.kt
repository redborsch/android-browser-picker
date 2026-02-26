package com.github.redborsch.browserpicker.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.customizer.CustomizerActivity
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog
import com.github.redborsch.graphics.max
import com.github.redborsch.log.getLogger
import com.github.redborsch.preferences.DimensionPreference
import com.github.redborsch.preferences.EditTextPreferenceDialogWithValidation
import com.github.redborsch.preferences.ValidationStrategy
import com.github.redborsch.preferences.withPreference
import com.github.redborsch.window.currentWindowBounds
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

class SettingsFragment :
    PreferenceFragmentCompat(),
    PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback {

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(
            menu: Menu,
            menuInflater: MenuInflater
        ) {
            menuInflater.inflate(R.menu.settings, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.menu_reset_preferences -> resetPreferences()
                else -> return false
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
        addPreferencesFromResource(R.xml.settings)

        setupPreferences()
    }

    private fun resetPreferences() {
        childFragmentManager.showDialog(defaultFragmentTag) {
            ResetSettingsWarningDialogFragment()
        }
    }

    override fun onPreferenceDisplayDialog(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        when (pref.key) {
            getString(R.string.pref_key_test_url) -> showUrlEditor(pref)
            else -> return false
        }
        return true
    }

    private fun setupPreferences() {
        withPreference<Preference>(R.string.pref_key_browser_list_launcher) {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                launchBrowserListCustomizer()
                true
            }
        }

        withPreference<DimensionPreference>(R.string.pref_key_peek_height) {
            max = (requireContext().currentWindowBounds.max * Globals.MAX_COLLAPSED_BOTTOM_SHEET_HEIGHT).roundToInt()
            min = resources.getDimension(R.dimen.pref_min_peek_height).roundToInt()
        }
    }

    private fun launchBrowserListCustomizer() {
        val context = context ?: return
        startActivity(Intent(context, CustomizerActivity::class.java))
    }

    private fun showUrlEditor(pref: Preference) {
        parentFragmentManager.showDialog(defaultFragmentTag) {
            EditTextPreferenceDialogWithValidation.newInstance(
                pref.key,
                UrlValidationStrategy(),
            ).also {
                // Used internally by the AndroidX preference library
                @Suppress("DEPRECATION")
                it.setTargetFragment(this, 0)
            }
        }
    }

    interface Host {
        /**
         * We cannot simply reload [PreferenceFragmentCompat], so need Activity to replace it
         * with a new instance.
         */
        fun onSettingsReset()
    }
}

@Parcelize
private class UrlValidationStrategy : ValidationStrategy {

    @IgnoredOnParcel
    private val schemeRegex = "(HTTP|http)([Ss])?://.+".toRegex()

    @IgnoredOnParcel
    private val log = getLogger()

    override fun validate(text: CharSequence): Int? {
        if (text.isBlank()) {
            return R.string.url_validation_error_empty
        }
        if (!text.matches(schemeRegex)) {
            return R.string.url_validation_error_scheme
        }
        val parsed = runCatching {
            text.toString().toUri()
        }.getOrElse {
            return R.string.url_validation_error_general
        }
        log.v {
            buildString {
                appendLine("Parsed uri:")
                appendLine("  Scheme: ${parsed.scheme}")
                appendLine("  Host: ${parsed.host}")
                appendLine("  Path: ${parsed.path}")
                appendLine("  Query: ${parsed.query}")
                appendLine("  Fragment: ${parsed.fragment}")
            }
        }
        return null
    }
}
