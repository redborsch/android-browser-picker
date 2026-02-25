package com.github.redborsch.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.github.redborsch.binding.ViewBindingInflate
import com.github.redborsch.insets.applyEdgeToEdgePatches
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BottomSheetDialogFragment<VB : ViewBinding>(
    private val factory: ViewBindingInflate<VB>,
) : BottomSheetDialogFragment() {

    final override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = factory(layoutInflater, null, false)
        return BottomSheetDialog(requireContext(), theme).apply {
            dismissWithAnimation = true
            setContentView(binding.root)
            applyEdgeToEdgePatches(window!!)
            binding.setUp(this)
        }
    }

    protected abstract fun VB.setUp(lifecycleOwner: LifecycleOwner)
}
