package com.github.redborsch.browserpicker.chooser

import android.content.DialogInterface
import android.net.Uri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import com.github.redborsch.browserpicker.common.Settings
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.fragment.BottomSheetDialogFragment
import com.github.redborsch.insets.InsetLocation
import com.github.redborsch.insets.applyBottomSheetPaddings
import com.github.redborsch.insets.applyInsetsAsPaddings

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

    override fun FragmentBrowserChooserBinding.setUp(lifecycleOwner: LifecycleOwner) {
        val uri = retrieveUri() ?: return // Should not happen

        applySettings()

        link.text = uri.toString()

        BrowserListHelper(viewModel)
            .setUp(
                this@BrowserChooserBottomSheetFragment,
                browserList,
                lifecycleOwner,
                uri,
            )
        scrolledContent.applyBottomSheetPaddings()
    }

    private fun FragmentBrowserChooserBinding.applySettings() {
        val settings = Settings.getInstance(requireContext())

        if (settings.truncateLink) {
            link.maxLines = settings.maxLinkLines
        }
    }

    private fun retrieveUri(): Uri? = activity?.run { intent.data }
}