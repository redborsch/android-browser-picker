package com.github.redborsch.browserpicker

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.github.redborsch.browserpicker.shared.fragment.ViewBindingFragment
import com.github.redborsch.browserpicker.shared.fragment.attachBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BrowserChooserBottomSheetFragment :
    BottomSheetDialogFragment(),
    ViewBindingFragment<FragmentBrowserChooserBinding> {

    private val viewModel: BrowserChooserViewModel by activityViewModels()

    private var destroyingDialog = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = attachBinding(
        FragmentBrowserChooserBinding.inflate(inflater, container, false)
    )

    override fun FragmentBrowserChooserBinding.setUp() {
        BrowserChooserUiHelper(viewModel)
            .setUp(requireActivity(), this, lifecycleScope, retrieveUri())
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

    private fun retrieveUri(): Uri =
        requireActivity().intent.data
            // Should not happen!
            ?: "https://mozilla.org".toUri()
}