package com.github.redborsch.browserpicker.chooser

import androidx.fragment.app.activityViewModels
import com.github.redborsch.binding.ViewBindingFragment
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.insets.InsetLocation
import com.github.redborsch.insets.applyInsetsAsMargins
import com.github.redborsch.insets.applyInsetsAsPaddings

class BrowserChooserFragment : ViewBindingFragment<FragmentBrowserChooserBinding>(
    FragmentBrowserChooserBinding::inflate
) {
    private val viewModel: BrowserChooserViewModel by activityViewModels()

    override fun FragmentBrowserChooserBinding.setUp() {
        toolbar.applyInsetsAsMargins(InsetLocation { LEFT + RIGHT + TOP })
        scrolledContent.applyInsetsAsPaddings(InsetLocation { LEFT + RIGHT + BOTTOM })

        toolbar.setNavigationOnClickListener {
            activity?.finish()
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
