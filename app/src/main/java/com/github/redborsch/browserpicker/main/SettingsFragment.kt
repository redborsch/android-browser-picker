package com.github.redborsch.browserpicker.main

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.redborsch.browserpicker.R
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.common.createChooserIntent
import com.github.redborsch.preferences.EditTextPreferenceDialogWithValidation
import com.github.redborsch.fragment.defaultFragmentTag
import com.github.redborsch.fragment.showDialog
import com.github.redborsch.preferences.ValidationStrategy
import kotlinx.parcelize.Parcelize
import androidx.core.net.toUri
import com.github.redborsch.log.getLogger
import kotlinx.parcelize.IgnoredOnParcel

class SettingsFragment : PreferenceFragmentCompat(),
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
                R.id.menu_test_it -> openChooser()
                R.id.menu_reset_preferences -> resetPreferences()
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
    }

    private fun openChooser() {
        val context = context ?: return
        startActivity(createChooserIntent(context))
    }

    private fun resetPreferences() {
        val context = context ?: return
        // TODO show dialog
        Settings.getInstance(context).clear()

        (activity as? Host)?.onSettingsReset()
    }

    override fun onPreferenceDisplayDialog(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        if (pref.key == getString(R.string.pref_key_test_url)) {
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
            return true
        }
        return false
    }

    interface Host {
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
