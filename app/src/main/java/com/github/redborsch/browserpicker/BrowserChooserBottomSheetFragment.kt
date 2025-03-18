package com.github.redborsch.browserpicker

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.redborsch.browserpicker.databinding.FragmentBrowserChooserBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.core.net.toUri

class BrowserChooserBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: BrowserChooserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBrowserChooserBinding.inflate(inflater, container, false)
        BrowserChooserUiHelper(viewModel)
            .setUp(requireActivity(), binding, viewLifecycleOwner.lifecycleScope, retrieveUri())
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity?.finish()
    }

    private fun retrieveUri(): Uri =
        requireActivity().intent.data
            // Should not happen!
            ?: "https://mozilla.org".toUri()
}