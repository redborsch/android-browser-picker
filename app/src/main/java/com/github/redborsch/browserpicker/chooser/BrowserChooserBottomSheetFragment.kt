package com.github.redborsch.browserpicker.chooser

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.fragment.BottomSheetDialogFragment
import com.github.redborsch.insets.applyBottomSheetPaddings
import com.github.redborsch.os.requireParcelable
import com.github.redborsch.window.currentWindowBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class BrowserChooserBottomSheetFragment : BottomSheetDialogFragment<FragmentBrowserChooserBinding>(
    FragmentBrowserChooserBinding::inflate
) {

    private val viewModel: BrowserChooserViewModel by activityViewModels()

    private lateinit var browserIntentFactory: BrowserIntentFactory

    private lateinit var settings: Settings

    private var destroyingDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        browserIntentFactory = requireArguments().requireParcelable(ARG_BROWSER_INTENT_FACTORY)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        settings = Settings.getInstance(context)
    }

    override fun onDestroyView() {
        // DialogFragment automatically destroys the dialog when the fragment view is destroyed
        // + calls onDismiss. We need to differentiate between when the dialog is dismissed by
        // the user and when it is dismissed automatically.
        destroyingDialog = true
        try {
            super.onDestroyView()
        } finally {
            destroyingDialog = false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (!destroyingDialog) {
            activity?.finishAndRemoveTask()
        }
    }

    override fun FragmentBrowserChooserBinding.setUp(dialog: BottomSheetDialog) {
        applySettings(dialog)

        link.text = browserIntentFactory.uri.toString()

        BrowserListHelper(viewModel)
            .setUp(
                this@BrowserChooserBottomSheetFragment,
                browserList,
                dialog,
                browserIntentFactory,
            )

        scrolledContent.applyBottomSheetPaddings()
    }

    private fun FragmentBrowserChooserBinding.applySettings(dialog: BottomSheetDialog) {
        if (settings.truncateLink) {
            link.maxLines = settings.maxLinkLines
        }
        with(dialog.behavior) {
            peekHeight = settings.peekHeight.coerceAtMost(dialog.context.maxPeekHeight)
            state = if (settings.fullScreenByDefault) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private val Context.maxPeekHeight: Int
        get() = (currentWindowBounds.height() * Globals.MAX_COLLAPSED_BOTTOM_SHEET_HEIGHT).roundToInt()

    companion object {

        private const val ARG_BROWSER_INTENT_FACTORY = "BrowserIntentFactory"

        fun newInstance(browserIntentFactory: BrowserIntentFactory): BrowserChooserBottomSheetFragment {
            return BrowserChooserBottomSheetFragment().apply {
                arguments = Bundle(1).apply {
                    putParcelable(ARG_BROWSER_INTENT_FACTORY, browserIntentFactory)
                }
            }
        }
    }
}
