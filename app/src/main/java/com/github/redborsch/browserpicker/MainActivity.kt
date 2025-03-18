package com.github.redborsch.browserpicker

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.github.redborsch.browserpicker.databinding.ActivityMainBinding

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tryItOut.setOnClickListener {
            openChooser("https://play.google.com/store/apps/details?id=com.github.redborsch.browserpicker".toUri())
        }

        binding.makeDefault.setOnClickListener {
            makeDefault()
        }
    }

    private fun openChooser(url: Uri) {
        Intent(this, ChooserActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = url
        }.let {
            startActivity(it)
        }
    }

    // The following code was adapted from Firefox Focus: https://github.com/mozilla-mobile/focus-android
    private fun makeDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(RoleManager::class.java).also {
                if (it.isRoleAvailable(RoleManager.ROLE_BROWSER) && !it.isRoleHeld(
                        RoleManager.ROLE_BROWSER,
                    )
                ) {
                    startActivityForResult(
                        it.createRequestRoleIntent(RoleManager.ROLE_BROWSER),
                        REQUEST_CODE_BROWSER_ROLE,
                    )
                } else {
                    navigateToDefaultBrowserAppsSettings()
                }
            }
        } else {
            navigateToDefaultBrowserAppsSettings()
        }
    }

    private fun navigateToDefaultBrowserAppsSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
        intent.putExtra(
            SETTINGS_SELECT_OPTION_KEY,
            DEFAULT_BROWSER_APP_OPTION,
        )
        intent.putExtra(
            SETTINGS_SHOW_FRAGMENT_ARGS,
            bundleOf(SETTINGS_SELECT_OPTION_KEY to DEFAULT_BROWSER_APP_OPTION),
        )

        startActivity(intent)
    }

    companion object {
        private const val REQUEST_CODE_BROWSER_ROLE = 1
        private const val SETTINGS_SELECT_OPTION_KEY = ":settings:fragment_args_key"
        private const val SETTINGS_SHOW_FRAGMENT_ARGS = ":settings:show_fragment_args"
        private const val DEFAULT_BROWSER_APP_OPTION = "default_browser"
    }
}