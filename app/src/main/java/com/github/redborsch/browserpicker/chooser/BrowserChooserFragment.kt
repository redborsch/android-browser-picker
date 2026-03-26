package com.github.redborsch.browserpicker.chooser

import androidx.fragment.app.activityViewModels
import com.github.redborsch.binding.ViewBindingFragment
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.insets.InsetLocation
import com.github.redborsch.insets.applyDefaults
import com.github.redborsch.insets.applyInsetsAsPaddings

class BrowserChooserFragment : ViewBindingFragment<FragmentBrowserChooserBinding>(
    FragmentBrowserChooserBinding::inflate
) {
    private val viewModel: BrowserChooserViewModel by activityViewModels()

    override fun FragmentBrowserChooserBinding.setUp() {
        browserList.applyInsetsAsPaddings(InsetLocation { LEFT + RIGHT + BOTTOM })
        protectionLayout.applyDefaults()

        toolbar.setNavigationOnClickListener {
            activity?.finishAndRemoveTask()
        }

        BrowserListHelper(
            viewModel,
            this@BrowserChooserFragment,
            viewLifecycleOwner,
            browserList,
            link,
        )
    }
}
