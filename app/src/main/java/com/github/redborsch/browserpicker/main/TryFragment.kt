package com.github.redborsch.browserpicker.main

import android.animation.LayoutTransition
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.redborsch.binding.ViewBindingFragment
import com.github.redborsch.browserpicker.ChooserActivity
import com.github.redborsch.browserpicker.databinding.FragmentMainTryBinding
import com.github.redborsch.browserpicker.model.SetupViewModel
import com.github.redborsch.insets.InsetLocation
import com.github.redborsch.insets.applyInsetsAsPaddings
import com.github.redborsch.lifecycle.launchOnEachStart

class TryFragment : ViewBindingFragment<FragmentMainTryBinding>(
    FragmentMainTryBinding::inflate
) {

    private val setupViewModel: SetupViewModel by activityViewModels()
    private val viewModel: TryViewModel by viewModels {
        TryViewModel.createFactory(setupViewModel.isDefault)
    }

    private val openChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        viewModel.registerTryAttempt()
    }

    override fun FragmentMainTryBinding.setUp() {
        tryItOut.setOnClickListener {
            openChooser()
        }

        viewLifecycleOwner.launchOnEachStart {
            viewModel.shouldShowDefaultBrowserHint.collect { isTried ->
                if (isTried && root.layoutTransition == null) {
                    root.layoutTransition = LayoutTransition().apply {
                        enableTransitionType(LayoutTransition.APPEARING)
                    }
                }
                setupInto.isVisible = isTried
            }
        }
        root.applyInsetsAsPaddings(InsetLocation.BOTTOM)
    }

    private fun openChooser() {
        val context = context ?: return
        openChooser.launch(ChooserActivity.createIntent(context))
    }
}
