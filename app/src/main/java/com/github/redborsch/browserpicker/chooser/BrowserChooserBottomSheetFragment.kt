package com.github.redborsch.browserpicker.chooser

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import androidx.fragment.app.activityViewModels
import com.github.redborsch.browserpicker.common.Globals
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.fragment.BottomSheetDialogFragment
import com.github.redborsch.insets.applyBottomSheetPaddings
import com.github.redborsch.window.currentWindowBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class BrowserChooserBottomSheetFragment : BottomSheetDialogFragment<FragmentBrowserChooserBinding>(
    FragmentBrowserChooserBinding::inflate
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

    override fun FragmentBrowserChooserBinding.setUp(dialog: BottomSheetDialog) {
        val uri = retrieveUri() ?: return // Should not happen

        applySettings(dialog)

        link.text = uri.toString()

        BrowserListHelper(viewModel)
            .setUp(
                this@BrowserChooserBottomSheetFragment,
                browserList,
                dialog,
                uri,
            )
        scrolledContent.applyBottomSheetPaddings()
    }

    private fun FragmentBrowserChooserBinding.applySettings(dialog: BottomSheetDialog) {
        val context = dialog.context
        val settings = Settings.getInstance(context)

        if (settings.truncateLink) {
            link.maxLines = settings.maxLinkLines
        }
        with(dialog.behavior) {
            peekHeight = settings.peekHeight.coerceAtMost(context.maxPeekHeight)
            state = if (settings.fullScreenByDefault) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun retrieveUri(): Uri? = activity?.run { intent.data }

    private val Context.maxPeekHeight: Int
        get() = (currentWindowBounds.height() * Globals.MAX_COLLAPSED_BOTTOM_SHEET_HEIGHT).roundToInt()
}