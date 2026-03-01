package com.github.redborsch.browserpicker.setup

import androidx.fragment.app.activityViewModels
import com.github.redborsch.binding.ViewBindingFragment
import com.github.redborsch.browserpicker.databinding.FragmentSetupActionRequiredBinding
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.browserpicker.shared.system.browser.BrowserRequestSucceededListener

class MakeDefaultFragment : ViewBindingFragment<FragmentSetupActionRequiredBinding>(
    FragmentSetupActionRequiredBinding::inflate
), BrowserRequestSucceededListener {

    private val viewModel: SetupViewModel by activityViewModels()
    private val defaultBrowserActionHandler = DefaultBrowserActionHandler(this)

    override fun FragmentSetupActionRequiredBinding.setUp() {
        makeDefault.setOnClickListener {
            defaultBrowserActionHandler.launchDefaultBrowserSettings()
        }
    }

    override fun onBrowserRequestSucceeded() {
        viewModel.refreshDefaultState()
    }
}
