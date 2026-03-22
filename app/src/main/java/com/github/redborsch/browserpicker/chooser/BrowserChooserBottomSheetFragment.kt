package com.github.redborsch.browserpicker.chooser

import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.activityViewModels
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBottomSheetBinding
import com.github.redborsch.fragment.BottomSheetDialogFragment
import com.github.redborsch.insets.applyBottomSheetPaddings
import com.github.redborsch.window.currentWindowBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class BrowserChooserBottomSheetFragment :
    BottomSheetDialogFragment<FragmentBrowserChooserBottomSheetBinding>(
        FragmentBrowserChooserBottomSheetBinding::inflate
    ) {

    private val viewModel: BrowserChooserViewModel by activityViewModels()

    private var destroyingDialog = false

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

    override fun FragmentBrowserChooserBottomSheetBinding.setUp(dialog: BottomSheetDialog) {
        dialog.behavior.applySettings(viewModel.settings, dialog.context)
        scrolledContent.applyBottomSheetPaddings()

        BrowserListHelper(
            viewModel,
            this@BrowserChooserBottomSheetFragment,
            dialog,
            browserList,
            link,
        )
    }

    private fun BottomSheetBehavior<*>.applySettings(settings: Settings, context: Context) {
        peekHeight = settings.peekHeight.coerceAtMost(context.maxPeekHeight)
        state = if (settings.fullScreenByDefault) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private val Context.maxPeekHeight: Int
        get() = (currentWindowBounds.height() * Globals.MAX_COLLAPSED_BOTTOM_SHEET_HEIGHT).roundToInt()
}
