package com.github.redborsch.browserpicker.setup

import androidx.fragment.app.activityViewModels
import com.github.redborsch.binding.ViewBindingFragment
import com.github.redborsch.browserpicker.databinding.FragmentSetupAllSetBinding
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.browserpicker.shared.system.browser.BrowserRequestSucceededListener
import kotlin.getValue

class AllSetFragment : ViewBindingFragment<FragmentSetupAllSetBinding>(
    FragmentSetupAllSetBinding::inflate
), BrowserRequestSucceededListener {

    private val viewModel: SetupViewModel by activityViewModels()
    private val defaultBrowserActionHandler = DefaultBrowserActionHandler(this)

    override fun FragmentSetupAllSetBinding.setUp() {
        openDefaultBrowserSettings.setOnClickListener {
            defaultBrowserActionHandler.launchDefaultBrowserSettings(force = true)
        }
    }

    override fun onBrowserRequestSucceeded() {
        viewModel.refreshDefaultState()
    }
}
