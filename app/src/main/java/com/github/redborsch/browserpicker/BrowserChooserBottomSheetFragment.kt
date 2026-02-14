package com.github.redborsch.browserpicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.internal.EdgeToEdgeUtils

class BrowserChooserBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: BrowserChooserViewModel by activityViewModels()

    private var destroyingDialog = false

    @SuppressLint("RestrictedApi", "WrongConstant")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setContentView(R.layout.fragment_browser_chooser)

            // Edge-to-edge logic was borrowed from
            // https://github.com/material-components/material-components-android
            EdgeToEdgeUtils.applyEdgeToEdge(window!!, true)

            ViewCompat.setOnApplyWindowInsetsListener(window!!.decorView) { v, insets ->
                val leftInset: Int
                val rightInset: Int
                if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
                    leftInset = insets.getInsets(WindowInsets.Type.systemBars()).left
                    rightInset = insets.getInsets(WindowInsets.Type.systemBars()).right
                } else {
                    leftInset = insets.stableInsetLeft
                    rightInset = insets.stableInsetRight
                }

                v.setPadding(leftInset, 0, rightInset, 0)
                insets
            }

            val root = findViewById<View>(R.id.chooser_root)!!
            val binding = FragmentBrowserChooserBinding.bind(root)
            binding.setUp()
        }
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

    private fun FragmentBrowserChooserBinding.setUp() {
        BrowserChooserUiHelper(viewModel)
            .setUp(requireActivity(), this, lifecycleScope, retrieveUri())
    }

    private fun retrieveUri(): Uri =
        requireActivity().intent.data
        // Should not happen!
            ?: "https://mozilla.org".toUri()
}