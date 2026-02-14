package com.github.redborsch.browserpicker.system

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.redborsch.browserpicker.databinding.FragmentActionRequiredBinding
import com.github.redborsch.browserpicker.shared.fragment.ViewBindingFragment
import com.github.redborsch.browserpicker.shared.fragment.attachBinding

class MakeDefaultFragment : Fragment(), ViewBindingFragment<FragmentActionRequiredBinding> {

    private val viewModel: SystemCheckViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = attachBinding(
        FragmentActionRequiredBinding.inflate(inflater, container, false)
    )

    override fun FragmentActionRequiredBinding.setUp() {
        makeDefault.setOnClickListener {
            activity?.makeDefault()
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_BROWSER_ROLE) {
            viewModel.refreshDefaultState()
        } else {
            @Suppress("DEPRECATION")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // The following code was adapted from Firefox Focus: https://github.com/mozilla-mobile/focus-android
    private fun Context.makeDefault() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(RoleManager::class.java).also {
                if (it.isRoleAvailable(RoleManager.ROLE_BROWSER) && !it.isRoleHeld(
                        RoleManager.ROLE_BROWSER,
                    )
                ) {
                    @Suppress("DEPRECATION")
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
