package com.github.redborsch.browserpicker.setup

import androidx.fragment.app.activityViewModels
import com.github.redborsch.browserpicker.databinding.FragmentSetupActionRequiredBinding
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.browserpicker.shared.system.DefaultBrowserAction
import com.github.redborsch.binding.ViewBindingFragment

class MakeDefaultFragment : ViewBindingFragment<FragmentSetupActionRequiredBinding>(
    FragmentSetupActionRequiredBinding::inflate
) {

    private val viewModel: SetupViewModel by activityViewModels()

    private val defaultBrowserAction = DefaultBrowserAction(this).apply {
        onBrowserRequestSucceeded {
            viewModel.refreshDefaultState()
        }
        onSettingsLaunchFailed {
            SettingsErrorDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun FragmentSetupActionRequiredBinding.setUp() {
        makeDefault.setOnClickListener {
            defaultBrowserAction.launchDefaultBrowserSettings()
        }
    }
}
